package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import org.sailcbi.APIServer.CbiUtil.Currency

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
