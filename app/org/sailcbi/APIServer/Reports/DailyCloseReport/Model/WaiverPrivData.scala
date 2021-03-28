package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import com.coleji.framework.Util.Currency

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