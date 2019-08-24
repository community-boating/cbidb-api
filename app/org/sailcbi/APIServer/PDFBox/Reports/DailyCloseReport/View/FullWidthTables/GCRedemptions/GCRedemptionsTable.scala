package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FullWidthTables.GCRedemptions

import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable._
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.GCRedemptionData
import org.apache.pdfbox.pdmodel.font.PDFont

class GCRedemptionsTable(
	signups: List[GCRedemptionData],
	headerFont: PDFont,
	bodyFont: PDFont,
	fontSize: Float
) extends AbstractTable[GCRedemptionData](
	signups,
	new MultiDrawableTable(List(DrawableTable(
		List(List("GIFT CERTIFICATES - REDEMPTIONS")),
		List(500f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Last Name", "First Name", "Cert #", "Used For", "Amount")),
		List(125f, 125f, 75f, 100f, 75f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_LEFT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
)