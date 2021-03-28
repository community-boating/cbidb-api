package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import org.sailcbi.APIServer.CbiUtil.Currency

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
