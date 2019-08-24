package org.sailcbi.APIServer.PDFBox.Reports.JpSpecialNeedsReport.Model

import org.sailcbi.APIServer.PDFBox.Abstract.RowData

case class JpSpecialNeedsData(
	personId: Int,
	firstName: String,
	lastName: String,
	allergies: Option[String],
	medications: Option[String],
	specialNeeds: Option[String]
) extends RowData {
	override def cellValues: List[String] = List(
		lastName + ", " + firstName,
		allergies.getOrElse(""),
		medications.getOrElse(""),
		specialNeeds.getOrElse("")
	)
}