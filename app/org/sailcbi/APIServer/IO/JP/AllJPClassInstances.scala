package org.sailcbi.APIServer.IO.JP

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Storable.StorableQuery.{QueryBuilder, TableAlias}
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
			.where(JpClassSession.fields.sessionDateTime.alias.isYearConstant(PA.currentSeason()))

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
			val sorted = sessions.sortWith((a, b) => a.values.sessionDateTime.get.isBefore(b.values.sessionDateTime.get))
			val min = sorted.head.values.sessionDateTime.get
			val max = sorted.last.values.sessionDateTime.get
			val sessionLength = sorted.head.values.lengthOverride.get match {
				case Some(d) => d
				case None => instance.references.jpClassType.get.values.sessionlength.get
			}
			(instance, min, max, sessionLength)
		})).toList
	}
}
