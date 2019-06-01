package PDFBox.Reports.DailyCloseReport.View.FullWidthTables.WaiversPrivsCards

import PDFBox.Abstract.AbstractTable
import PDFBox.Drawable._
import PDFBox.Reports.DailyCloseReport.Model.ReplacementCardData
import org.apache.pdfbox.pdmodel.font.PDFont

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