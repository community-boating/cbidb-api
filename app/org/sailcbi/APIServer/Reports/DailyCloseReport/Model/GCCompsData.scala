package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import com.coleji.framework.PDFBox.Abstract.RowData

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
