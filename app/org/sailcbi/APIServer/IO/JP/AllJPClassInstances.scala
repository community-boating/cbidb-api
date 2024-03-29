package org.sailcbi.APIServer.IO.JP

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession, JpClassType}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

import java.time.LocalDateTime

object AllJPClassInstances {
	def get(rc: StaffRequestCache)(implicit PA: PermissionsAuthority): List[(JpClassInstance, LocalDateTime, LocalDateTime, Double)] = {
		val types = JpClassType.alias
		val instances = JpClassInstance.alias
		val sessions = JpClassSession.alias

		val sessionsQB = QueryBuilder
			.from(types)
			.innerJoin(instances, JpClassType.fields.typeId.alias equalsField JpClassInstance.fields.typeId.alias)
			.innerJoin(sessions, JpClassInstance.fields.instanceId.alias equalsField JpClassSession.fields.instanceId.alias)
			.where(JpClassSession.fields.sessionDatetime.alias.isYearConstant(PA.currentSeason()))
			.select(
				types.wrappedFields(f => List(f.typeId, f.typeName, f.displayOrder, f.sessionLength)) ++
				instances.wrappedFields(f => List(f.instanceId, f.typeId, f.instructorId, f.locationId, f.adminHold)) ++
				sessions.wrappedFields(f => List(f.sessionId, f.instanceId, f.sessionDatetime, f.lengthOverride))
			)

		val allSessions = rc.executeQueryBuilder(sessionsQB).map(qbrr => {
			val session = JpClassSession.construct(qbrr)
			val instance = JpClassInstance.construct(qbrr)
			val classType = JpClassType.construct(qbrr)
			session.references.jpClassInstance.set(instance)
			instance.references.jpClassType.set(classType)
			session
		})

		val groupedSessions = allSessions.groupBy(_.references.jpClassInstance.get)
		groupedSessions.map(Function.tupled((instance, sessions) => {
			val sorted = sessions.sortWith((a, b) => a.values.sessionDatetime.get.isBefore(b.values.sessionDatetime.get))
			val min = sorted.head.values.sessionDatetime.get
			val max = sorted.last.values.sessionDatetime.get
			val sessionLength = sorted.head.values.lengthOverride.get match {
				case Some(d) => d
				case None => instance.references.jpClassType.get.values.sessionLength.get
			}
			(instance, min, max, sessionLength)
		})).toList
	}
}
