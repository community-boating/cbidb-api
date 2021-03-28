package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import com.coleji.framework.Util.Currency

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
