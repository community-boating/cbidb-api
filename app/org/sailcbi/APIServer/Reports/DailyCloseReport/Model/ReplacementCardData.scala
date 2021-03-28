package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import com.coleji.framework.Util.Currency

class ReplacementCardData(
	val fullName: String,
	val price: Currency
) extends RowData {
	val cellValues = List(
		fullName,
		price.format()
	)
}
