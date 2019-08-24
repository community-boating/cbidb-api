package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

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