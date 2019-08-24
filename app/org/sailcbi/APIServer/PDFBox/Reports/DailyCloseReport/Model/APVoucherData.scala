package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class APVoucherData(
						   val lastName: String,
						   val firstName: String,
						   val price: Currency,
						   val isVoid: Boolean
				   ) extends RowData {
	val cellValues = List(
		lastName,
		firstName,
		price.toString,
		if (isVoid) "(VOID)" else ""
	)
}