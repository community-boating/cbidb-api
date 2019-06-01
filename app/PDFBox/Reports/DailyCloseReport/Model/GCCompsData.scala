package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class GCCompsData(
						 val recipient: String,
						 val certNumber: String,
						 val value: Currency
				 ) extends RowData {
	val cellValues = List(
		recipient,
		certNumber,
		value.format()
	)
}
