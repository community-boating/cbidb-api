package IO.PreparedQueries.Apex.JpSpecialNeedsReport

import java.sql.ResultSet
import java.time.ZonedDateTime

import CbiUtil.{GetSQLLiteral, NAStrings}
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.JpSpecialNeedsReport.Model.JpSpecialNeedsData
import Services.Authentication.ApexUserType

class GetSpecialNeedsData(from: ZonedDateTime, to: ZonedDateTime) extends HardcodedQueryForSelect[JpSpecialNeedsData](Set(ApexUserType)) {
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

  override def mapResultSetRowToCaseObject(rs: ResultSet): JpSpecialNeedsData = JpSpecialNeedsData(
    personId = rs.getInt(1),
    firstName = rs.getString(2),
    lastName = rs.getString(3),
    allergies = NAStrings.nullifyNAString(rs.getOptionString(4)),
    medications = NAStrings.nullifyNAString(rs.getOptionString(5)),
    specialNeeds = NAStrings.nullifyNAString(rs.getOptionString(6))
  )
}