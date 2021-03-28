package org.sailcbi.APIServer.Reports.DailyCloseReport.View.ChecksARPage

import org.apache.pdfbox.pdmodel.font.PDFont
import com.coleji.framework.PDFBox.Abstract.AbstractTable
import com.coleji.framework.PDFBox.Drawable.{ALIGN_CENTER, ALIGN_RIGHT, DrawableTable, MultiDrawableTable}
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.Check

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
