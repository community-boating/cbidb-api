package PDFBox.Reports.DailyCloseReport.View.ChecksARPage

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable._
import PDFBox.Reports.DailyCloseReport.Model.AR
import org.apache.pdfbox.pdmodel.font.PDFont

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
