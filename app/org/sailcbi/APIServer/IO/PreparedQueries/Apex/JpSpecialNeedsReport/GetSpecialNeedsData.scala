package org.sailcbi.APIServer.IO.PreparedQueries.Apex.JpSpecialNeedsReport

import com.coleji.framework.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.CbiUtil.{GetSQLLiteral, NAStrings}
import org.sailcbi.APIServer.Reports.JpSpecialNeedsReport.Model.JpSpecialNeedsData
import org.sailcbi.APIServer.Services.Authentication.{ApexRequestCache, StaffRequestCache}
import org.sailcbi.APIServer.Services.ResultSetWrapper

import java.time.ZonedDateTime

class GetSpecialNeedsData(from: ZonedDateTime, to: ZonedDateTime) extends HardcodedQueryForSelect[JpSpecialNeedsData](Set(StaffRequestCache, ApexRequestCache)) {
	val fromLiteral: String = GetSQLLiteral(from, true)
	val toLiteral: String = GetSQLLiteral(to, true)
	val getQuery: String =
		s"""
		   |select distinct
		   |p.person_id,
		   |p.name_first,
		   |p.name_last,
		   |p.allergies,
		   |p.medications,
		   |p.special_needs
		   |from persons p, jp_class_signups si, jp_class_instances i, jp_class_sessions se
		   | where p.person_id = si.person_id
		   | and si.instance_id = i.instance_id
		   | and i.instance_id = se.instance_id
		   | and signup_type in ('E', 'W')
		   | and se.session_datetime >= $fromLiteral
		   | and se.session_datetime <= $toLiteral
		   | order by 3,2
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): JpSpecialNeedsData = JpSpecialNeedsData(
		personId = rs.getInt(1),
		firstName = rs.getString(2),
		lastName = rs.getString(3),
		allergies = NAStrings.nullifyNAString(rs.getOptionString(4)),
		medications = NAStrings.nullifyNAString(rs.getOptionString(5)),
		specialNeeds = NAStrings.nullifyNAString(rs.getOptionString(6))
	)
}