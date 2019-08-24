package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class GCSalesData(
						 val purchaser: String,
						 val recipient: String,
						 val certNumber: String,
						 val paid: Currency,
						 val value: Option[String] // say if they got a discount
				 ) extends RowData {
	val cellValues = List(
		purchaser,
		recipient,
		certNumber,
		paid.format(),
		value.getOrElse("")
	)
}
