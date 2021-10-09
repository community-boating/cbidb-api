package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FullWidthTables.WaiversPrivsCards

import com.coleji.neptune.PDFBox.Abstract.AbstractTable
import com.coleji.neptune.PDFBox.Drawable._
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.ReplacementCardData

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