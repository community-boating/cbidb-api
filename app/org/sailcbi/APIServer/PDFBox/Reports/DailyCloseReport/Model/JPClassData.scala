package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class JPClassData(
						 val lastName: String,
						 val firstName: String,
						 val className: String,
						 val firstSession: ZonedDateTime,
						 val discountName: Option[String],
						 val price: Currency
				 ) extends RowData {
	val cellValues = List(
		lastName,
		firstName,
		className,
		firstSession.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
		discountName.getOrElse(""),
		price.toString
	)
}
