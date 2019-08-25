package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.ChecksARPage

import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.PDFBox.Abstract.AbstractTable
import org.sailcbi.APIServer.PDFBox.Drawable._
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.AR

class ARTable(
					 ars: List[AR], defaultFont: PDFont, defaultFontSize: Float
			 ) extends AbstractTable[AR](
	ars,
	new MultiDrawableTable(List(DrawableTable(
		List(List("A/R Source", "Amount")),
		List(70f, 50f),
		List(ALIGN_CENTER, ALIGN_CENTER),
		defaultFont,
		defaultFontSize,
		1,
		3
	))),
	MultiDrawableTable.empty,
	List(ALIGN_LEFT, ALIGN_RIGHT),
	defaultFont,
	defaultFontSize,
	1,
	3
)
