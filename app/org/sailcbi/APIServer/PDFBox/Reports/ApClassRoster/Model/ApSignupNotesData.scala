package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model

import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class ApSignupNotesData(rd: ApRosterData) extends RowData {
	lazy val cellValues = {
		List(
			rd.lastName + ", " + rd.firstName,
			rd.signupNote.getOrElse("")
		)
	}
}