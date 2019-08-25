package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FullWidthTables.Donations

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable._
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.Donation

class DonationsTable(
							donations: List[Donation],
							headerFont: PDFont,
							bodyFont: PDFont,
							fontSize: Float
					) extends AbstractTable[Donation](
	donations,
	new MultiDrawableTable(List(DrawableTable(
		List(List("DONATIONS")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Name/Organization", "Fund", "Date", "In-Person / Online", "Amount")),
		List(175f, 100f, 75f, 75f, 75f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
) {
	val headers = List("Full Name", "Membership", "Discount", "In-Person / Online", "Price")
	val aligns = List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT)
}