package PDFBox.Reports.ApClassRoster.Model

import PDFBox.Abstract.RowData

class ApSignupNotesData(rd: ApRosterData) extends RowData {
  lazy val cellValues = {
    List(
      rd.lastName + ", " + rd.firstName,
      rd.signupNote.getOrElse("")
    )
  }
}