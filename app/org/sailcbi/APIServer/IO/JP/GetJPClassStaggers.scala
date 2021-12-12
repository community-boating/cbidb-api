package org.sailcbi.APIServer.IO.JP

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions.JpClassStagger
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

object GetJPClassStaggers {
	def get(rc: StaffRequestCache, instanceIds: List[Int])(implicit PA: PermissionsAuthority): List[JpClassStagger] = {
		val staggers = TableAlias.wrapForInnerJoin(JpClassStagger)
		val sessionsQB = QueryBuilder
			.from(staggers)
			.where(staggers.wrappedFields(_.instanceId).wrapFilter(_.inList(instanceIds)))

		rc.executeQueryBuilder(sessionsQB).map(staggers.construct).asInstanceOf[List[JpClassStagger]]
	}
}
