package org.sailcbi.APIServer.PDFBox.Reports.JpClassRoster.Model

import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class JpSignupNotesData(rd: JpRosterData) extends RowData {
	lazy val cellValues = {
		List(
			rd.lastName + ", " + rd.firstName,
			rd.signupNote.getOrElse("")
		)
	}
}