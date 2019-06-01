package PDFBox.Reports.JpClassRoster.Model

import PDFBox.Abstract.RowData

class JpSignupNotesData(rd: JpRosterData) extends RowData {
	lazy val cellValues = {
		List(
			rd.lastName + ", " + rd.firstName,
			rd.signupNote.getOrElse("")
		)
	}
}