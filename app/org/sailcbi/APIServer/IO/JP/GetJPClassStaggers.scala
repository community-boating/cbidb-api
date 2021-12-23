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
			.where(JpClassStagger.fields.instanceId.alias.inList(instanceIds))
			.select(staggers.wrappedFields(f => List(f.staggerId, f.instanceId, f.staggerDate, f.occupancy)))

		rc.executeQueryBuilder(sessionsQB).map(staggers.construct)
	}
}
