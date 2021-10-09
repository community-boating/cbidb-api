package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Currency

class ReplacementCardData(
	val fullName: String,
	val price: Currency
) extends RowData {
	val cellValues = List(
		fullName,
		price.format()
	)
}
