package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Currency

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
