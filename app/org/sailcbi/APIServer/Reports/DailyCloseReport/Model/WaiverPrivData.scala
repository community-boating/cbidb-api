package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Currency

class WaiverPrivData(
							val identifier: String,
							val location: String,
							val fullName: String,
							val price: Currency
					) extends RowData {
	val cellValues = List(
		identifier,
		location,
		fullName,
		price.format()
	)
}