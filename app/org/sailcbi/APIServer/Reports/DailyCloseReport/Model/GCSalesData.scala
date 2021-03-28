package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import com.coleji.framework.Util.Currency

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
