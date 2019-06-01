package IO.PreparedQueries.Apex.JpClassRoster

import java.sql.ResultSet

import CbiUtil.DateUtil
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.JpClassRoster.Model.JpClassInstanceData
import Services.Authentication.ApexUserType

class GetJpClassInstanceData(instanceId: Int) extends HardcodedQueryForSelect[JpClassInstanceData](Set(ApexUserType)) {
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

	override def mapResultSetRowToCaseObject(rs: ResultSet): JpClassInstanceData = JpClassInstanceData(
		rs.getInt(1),
		rs.getString(2),
		rs.getInt(3),
		DateUtil.toBostonTime(rs.getTimestamp(4).toLocalDateTime)
	)
}