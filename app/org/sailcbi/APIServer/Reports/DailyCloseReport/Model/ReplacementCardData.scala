package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import com.coleji.framework.PDFBox.Abstract.RowData

class ReplacementCardData(
	val fullName: String,
	val price: Currency
) extends RowData {
	val cellValues = List(
		fullName,
		price.format()
	)
}
