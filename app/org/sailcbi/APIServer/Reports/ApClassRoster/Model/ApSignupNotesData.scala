package org.sailcbi.APIServer.Reports.ApClassRoster.Model

import com.coleji.framework.PDFBox.Abstract.RowData

class ApSignupNotesData(rd: ApRosterData) extends RowData {
	lazy val cellValues = {
		List(
			rd.lastName + ", " + rd.firstName,
			rd.signupNote.getOrElse("")
		)
	}
}