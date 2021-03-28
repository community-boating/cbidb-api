package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import org.sailcbi.APIServer.CbiUtil.Currency

class ReplacementCardData(
	val fullName: String,
	val price: Currency
) extends RowData {
	val cellValues = List(
		fullName,
		price.format()
	)
}
