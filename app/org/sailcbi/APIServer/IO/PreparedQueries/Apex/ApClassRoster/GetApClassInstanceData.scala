package org.sailcbi.APIServer.IO.PreparedQueries.Apex.ApClassRoster

import org.sailcbi.APIServer.CbiUtil.DateUtil
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model.ApClassInstanceData
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, StaffUserType}
import org.sailcbi.APIServer.Services.ResultSetWrapper

class GetApClassInstanceData(instanceId: Int) extends HardcodedQueryForSelect[ApClassInstanceData](Set(StaffUserType, ApexUserType)) {
	val getQuery: String =
		s"""
		   |select
		   |i.instance_id,
		   | t.type_name,
		   |count(se.session_id),
		   |min(se.session_datetime)
		   |from ap_class_instances i, ap_class_sessions se, ap_class_formats f, ap_class_types t
		   | where i.instance_id = se.instance_id
		   | and i.format_id = f.format_id
		   | and f.type_id = t.type_id
		   | and i.instance_id = $instanceId
		   | group by i.instance_id, t.type_name
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): ApClassInstanceData = ApClassInstanceData(
		rs.getInt(1),
		rs.getString(2),
		rs.getInt(3),
		DateUtil.toBostonTime(rs.getLocalDateTime(4))
	)
}