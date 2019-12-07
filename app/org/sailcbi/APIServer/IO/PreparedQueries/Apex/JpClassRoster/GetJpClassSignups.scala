package org.sailcbi.APIServer.IO.PreparedQueries.Apex.JpClassRoster

import org.sailcbi.APIServer.CbiUtil.{Initializable, NAStrings}
import org.sailcbi.APIServer.PDFBox.Reports.JpClassRoster.Model.JpRosterData
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

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
		   |si.signup_note,
		   |sl.section_name
		   |from persons p
		   |inner join jp_class_signups si
		   |on p.person_id = si.person_id
		   |left outer join jp_class_sections sec
		   |on si.section_id = sec.section_id
		   |left outer join jp_class_section_lookup sl
		   |on sec.lookup_id = sl.section_id
		   | where signup_type in ('E', 'W')
		   | and si.instance_id = $instanceId
		   | order by p.name_last, p.name_first
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): JpRosterData = new JpRosterData(
		personId = rs.getInt(1),
		firstName = rs.getString(2),
		lastName = rs.getString(3),
		isWaitList = rs.getString(4) != "E",
		allergies = NAStrings.nullifyNAString(rs.getOptionString(5)),
		medications = NAStrings.nullifyNAString(rs.getOptionString(6)),
		specialNeeds = NAStrings.nullifyNAString(rs.getOptionString(7)),
		signupNote = rs.getOptionString(8),
		numberSessions = new Initializable[Int],
		sectionName = rs.getOptionString(9)
	)
}