package org.sailcbi.APIServer.Reports.JpClassRoster.Model

import com.coleji.framework.PDFBox.Abstract.RowData

class JpSignupNotesData(rd: JpRosterData) extends RowData {
	lazy val cellValues = {
		List(
			rd.lastName + ", " + rd.firstName,
			rd.signupNote.getOrElse("")
		)
	}
}