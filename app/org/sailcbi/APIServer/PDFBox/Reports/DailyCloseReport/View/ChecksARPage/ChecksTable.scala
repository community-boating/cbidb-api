package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.ChecksARPage

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, DrawableTable, MultiDrawableTable}
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.Check

class ChecksTable(
	checks: List[Check], defaultFont: PDFont, defaultFontSize: Float
) extends AbstractTable[Check](
	checks,
	new MultiDrawableTable(List(DrawableTable(
		List(List("Check #", "Amount", "Name / School", "Date")),
		List(50f, 70f, 170f, 70f),
		List(ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER, ALIGN_CENTER),
		defaultFont,
		defaultFontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT),
	defaultFont,
	defaultFontSize,
	1,
	3
)
