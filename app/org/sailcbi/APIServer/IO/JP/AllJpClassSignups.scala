package org.sailcbi.APIServer.IO.JP

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession, JpClassSignup, JpClassType, JpClassWlResult}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

import java.time.LocalDateTime

object AllJpClassSignups {
	def get(rc: StaffRequestCache, instanceIds: List[Int])(implicit PA: PermissionsAuthority): List[JpClassSignup] = {
		val types = TableAlias.wrapForInnerJoin(JpClassType)
		val instances = TableAlias.wrapForInnerJoin(JpClassInstance)
		val signups = TableAlias.wrapForInnerJoin(JpClassSignup)
		val wlResults = TableAlias.wrapForOuterJoin(JpClassWlResult)

		val sessionsQB = QueryBuilder
			.from(types)
			.innerJoin(instances, types.wrappedFields(_.fields.typeId).wrapFilter(_.equalsField(instances.wrappedFields(_.fields.typeId))))
			.innerJoin(signups, instances.wrappedFields(_.fields.instanceId).wrapFilter(_.equalsField(signups.wrappedFields(_.fields.instanceId))))
			.outerJoin(wlResults, signups.wrappedFields(_.fields.signupId).wrapFilter(_.equalsField(wlResults.wrappedFields(_.fields.signupId))))
			.where(instances.wrappedFields(_.fields.instanceId).wrapFilter(_.inList(instanceIds)))

		val allSignups = rc.executeQueryBuilder(sessionsQB).map(qbrr => {
			val signup = signups.construct(qbrr)
			val instance = instances.construct(qbrr)
			val classType = types.construct(qbrr)
			val wlResult = wlResults.construct(qbrr)

			signup.references.jpClassWlResult.set(wlResult)
			signup.references.jpClassInstance.set(instance)
			instance.references.jpClassType.set(classType)
			signup
		})
		allSignups
	}
}
