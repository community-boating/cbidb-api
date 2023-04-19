package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassInstance, ApClassSession, Person}

import java.time.{Duration, ZonedDateTime}

object ApClassInstancesThisSeason extends CacheableFactory[Null, IndexedSeq[ApClassInstance]] {
	override protected val lifetime: Duration = Duration.ofSeconds(10)

	override protected def calculateKey(config: Null): String = CacheKeys.apClassInstancesThisSeason

	override protected def generateResult(rc: RequestCache, config: Null): IndexedSeq[ApClassInstance] = {
		getObjects(rc.assertUnlocked)
	}

	def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): IndexedSeq[ApClassInstance] = {
		val currentSeason = PA.currentSeason()

		val sessions = rc.getObjectsByFilters(ApClassSession, List(ApClassSession.fields.sessionDatetime.alias.isYearConstant(currentSeason)), Set(
			ApClassSession.fields.sessionId,
			ApClassSession.fields.instanceId,
			ApClassSession.fields.sessionDatetime,
			ApClassSession.fields.headcount,
			ApClassSession.fields.cancelledDatetime,
			ApClassSession.fields.sessionLength,
			ApClassSession.fields.isMakeup,
		), 1200)

		val instanceIds = sessions.map(_.values.instanceId.get).distinct

		val instances = rc.getObjectsByFilters(ApClassInstance, List(ApClassInstance.fields.instanceId.alias.inList(instanceIds)), Set(
			ApClassInstance.fields.instanceId,
			ApClassInstance.fields.signupsStartOverride,
			ApClassInstance.fields.signupMin,
			ApClassInstance.fields.signupMax,
			ApClassInstance.fields.formatId,
			ApClassInstance.fields.cancelByOverride,
			ApClassInstance.fields.price,
			ApClassInstance.fields.cancelledDatetime,
			ApClassInstance.fields.hideOnline,
			ApClassInstance.fields.locationString,
			ApClassInstance.fields.doNotAutoCancel,
			ApClassInstance.fields.instructorId
		), 1200)

		val instructorIds = instances.filter(_.values.instructorId.get.isDefined).map(_.values.instructorId.get.get)

		val instructors = rc.getObjectsByFilters(Person, List(Person.fields.personId.alias.inList(instructorIds)), Set(
			Person.fields.personId,
			Person.fields.nameFirst,
			Person.fields.nameLast
		))

		implicit val ordering: Ordering[ZonedDateTime] = Ordering.by(_.toEpochSecond)

		instances.foreach(i => {
			val instanceId = i.values.instanceId.get
			i.references.apClassSessions.set(sessions
				.filter(_.values.instanceId.get == instanceId)
				.sortBy(s => DateUtil.toBostonTime(s.values.sessionDatetime.get))
				.toIndexedSeq
			)
			i.references.instructor.set(i.values.instructorId.get match {
				case None => None
				case Some(id) => instructors.find(_.values.personId.get == id)
			})
		})

		instances.toIndexedSeq
	}
}

