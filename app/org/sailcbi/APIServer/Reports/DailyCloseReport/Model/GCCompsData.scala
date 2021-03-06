package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import com.coleji.framework.Util.Currency

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
