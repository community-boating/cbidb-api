package org.sailcbi.APIServer.Reports.ApClassRoster.Model

import com.coleji.neptune.PDFBox.Abstract.RowData

class ApSpecialNeedsData(rd: ApRosterData) extends RowData {
	lazy val cellValues = {
		List(
			rd.lastName + ", " + rd.firstName,
			rd.allergies.getOrElse(""),
			rd.medications.getOrElse(""),
			rd.specialNeeds.getOrElse("")
		)
	}
}