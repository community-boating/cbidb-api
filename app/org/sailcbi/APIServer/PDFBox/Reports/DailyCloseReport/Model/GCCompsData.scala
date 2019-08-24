package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

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
