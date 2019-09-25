package org.sailcbi.APIServer.IO.PreparedQueries.Apex.ApClassRoster

import org.sailcbi.APIServer.CbiUtil.{Initializable, NAStrings}
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model.ApRosterData
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

class GetApClassSignups(instanceId: Int) extends HardcodedQueryForSelect[ApRosterData](Set(ApexUserType)) {
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
		   |from persons p, ap_class_signups si
		   | where p.person_id = si.person_id
		   | and signup_type in ('E', 'W')
		   | and si.instance_id = $instanceId
		   | order by si.sequence
    """.stripMargin

	override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): ApRosterData = new ApRosterData(
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
