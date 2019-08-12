package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class ReplacementCardData(
	val fullName: String,
	val price: Currency
) extends RowData {
	val cellValues = List(
		fullName,
		price.format()
	)
}
