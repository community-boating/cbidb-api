package org.sailcbi.APIServer.IO.JP

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions.JpClassStagger
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

object GetJPClassStaggers {
	def get(rc: StaffRequestCache, instanceIds: List[Int])(implicit PA: PermissionsAuthority): List[JpClassStagger] = {
		val staggers = TableAlias.wrapForInnerJoin(JpClassStagger)
		val sessionsQB = QueryBuilder
			.from(staggers)
			.where(staggers.wrappedFields(_.fields.instanceId).wrapFilter(_.inList(instanceIds)))

		rc.executeQueryBuilder(sessionsQB).map(staggers.construct).asInstanceOf[List[JpClassStagger]]
	}
}
