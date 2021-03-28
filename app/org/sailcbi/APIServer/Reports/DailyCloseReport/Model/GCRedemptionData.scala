package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import com.coleji.framework.Util.Currency

class GCRedemptionData(
							  val lastName: String,
							  val firstName: String,
							  val certNumber: String,
							  val usedFor: String,
							  val amount: Currency
					  ) extends RowData {
	val cellValues = List(
		lastName,
		firstName,
		certNumber,
		usedFor,
		amount.format()
	)
}
