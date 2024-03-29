package org.sailcbi.APIServer.IO.PreparedQueries.Apex.JpClassRoster

import com.coleji.neptune.IO.PreparedQueries.HardcodedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.DateUtil
import org.sailcbi.APIServer.Reports.JpClassRoster.Model.JpClassInstanceData
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, StaffRequestCache}

class GetJpClassInstanceData(instanceId: Int) extends HardcodedQueryForSelect[JpClassInstanceData](Set(StaffRequestCache, ApexRequestCache)) {
	val getQuery: String =
		s"""
		   |select
		   |i.instance_id,
		   | t.type_name,
		   |count(se.session_id),
		   |min(se.session_datetime)
		   |from jp_class_instances i, jp_class_sessions se, jp_class_types t
		   | where i.instance_id = se.instance_id
		   | and i.type_id = t.type_id
		   | and i.instance_id = $instanceId
		   | group by i.instance_id, t.type_name
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): JpClassInstanceData = JpClassInstanceData(
		rs.getInt(1),
		rs.getString(2),
		rs.getInt(3),
		DateUtil.toBostonTime(rs.getLocalDateTime(4))
	)
}