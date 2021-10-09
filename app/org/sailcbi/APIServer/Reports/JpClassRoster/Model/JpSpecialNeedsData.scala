package org.sailcbi.APIServer.Reports.JpClassRoster.Model

import com.coleji.neptune.PDFBox.Abstract.RowData

class JpSpecialNeedsData(rd: JpRosterData) extends RowData {
	lazy val cellValues = {
		List(
			rd.lastName + ", " + rd.firstName,
			rd.allergies.getOrElse(""),
			rd.medications.getOrElse(""),
			rd.specialNeeds.getOrElse("")
		)
	}
}