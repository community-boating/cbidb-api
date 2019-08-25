package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FullWidthTables.WaiversPrivsCards

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable._
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.ReplacementCardData

class ReplacementCardsTable(
	data: List[ReplacementCardData],
	headerFont: PDFont,
	bodyFont: PDFont,
	fontSize: Float
) extends AbstractTable[ReplacementCardData](
	data,
	new MultiDrawableTable(List(DrawableTable(
		List(List("REPLACEMENT CARDS")),
		List(200f),
		List(ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	), DrawableTable(
		List(List("Full Name", "Price")),
		List(150f, 50f),
		List(ALIGN_CENTER, ALIGN_CENTER),
		headerFont,
		fontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_RIGHT),
	bodyFont,
	fontSize,
	1,
	3
)