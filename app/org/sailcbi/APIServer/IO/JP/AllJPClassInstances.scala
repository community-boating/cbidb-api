package org.sailcbi.APIServer.IO.JP

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession, JpClassType}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

import java.time.LocalDateTime

object AllJPClassInstances {
	def get(rc: StaffRequestCache)(implicit PA: PermissionsAuthority): List[(JpClassInstance, LocalDateTime, LocalDateTime, Double)] = {
		val types = TableAlias.wrapForInnerJoin(JpClassType)
		val instances = TableAlias.wrapForInnerJoin(JpClassInstance)
		val sessions = TableAlias.wrapForInnerJoin(JpClassSession)

		val sessionsQB = QueryBuilder
			.from(types)
			.innerJoin(instances, types.wrappedFields(_.fields.typeId).wrapFilter(_.equalsField(instances.wrappedFields(_.fields.typeId))))
			.innerJoin(sessions, instances.wrappedFields(_.fields.instanceId).wrapFilter(_.equalsField(sessions.wrappedFields(_.fields.instanceId))))
			.where(sessions.wrappedFields(_.fields.sessionDateTime).wrapFilter(_.isYearConstant(PA.currentSeason())))

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
