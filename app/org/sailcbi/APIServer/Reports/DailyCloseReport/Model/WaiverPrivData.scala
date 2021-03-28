package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import com.coleji.framework.PDFBox.Abstract.RowData

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