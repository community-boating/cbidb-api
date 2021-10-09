package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Currency

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
