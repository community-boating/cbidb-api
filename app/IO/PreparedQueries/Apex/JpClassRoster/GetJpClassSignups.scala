package IO.PreparedQueries.Apex.JpClassRoster

import java.sql.ResultSet

import CbiUtil.{Initializable, NAStrings}
import IO.PreparedQueries.HardcodedQueryForSelect
import PDFBox.Reports.JpClassRoster.Model.JpRosterData
import Services.Authentication.ApexUserType

class GetJpClassSignups(instanceId: Int) extends HardcodedQueryForSelect[JpRosterData](Set(ApexUserType)) {
  val getQuery: String =
    s"""
       |select
       |p.person_id,
       |p.name_first,
       |p.name_last,
       |si.signup_type,
       |p.allergies,
       |p.medications,
       |p.special_needs,
       |si.signup_note
       |from persons p, jp_class_signups si
       | where p.person_id = si.person_id
       | and signup_type in ('E', 'W')
       | and si.instance_id = $instanceId
       | order by si.sequence
    """.stripMargin

  override def mapResultSetRowToCaseObject(rs: ResultSet): JpRosterData = new JpRosterData(
    personId = rs.getInt(1),
    firstName = rs.getString(2),
    lastName = rs.getString(3),
    isWaitList = rs.getString(4) != "E",
    allergies = NAStrings.nullifyNAString(rs.getOptionString(5)),
    medications = NAStrings.nullifyNAString(rs.getOptionString(6)),
    specialNeeds = NAStrings.nullifyNAString(rs.getOptionString(7)),
    signupNote = rs.getOptionString(8),
    numberSessions = new Initializable[Int]
  )
}