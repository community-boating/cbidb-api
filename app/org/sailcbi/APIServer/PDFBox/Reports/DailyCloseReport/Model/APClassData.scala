package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class APClassData(
						 val lastName: String,
						 val firstName: String,
						 val className: String,
						 val firstSession: ZonedDateTime,
						 val price: Currency,
						 val payment: String
				 ) extends RowData {
	val cellValues = List(
		lastName,
		firstName,
		className,
		firstSession.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
		price.toString,
		payment
	)
}
