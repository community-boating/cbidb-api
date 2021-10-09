package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Currency

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Donation(
					  val name: String,
					  val fundName: String,
					  val donationDate: ZonedDateTime,
					  val location: String,
					  val amount: Currency
			  ) extends RowData {
	val cellValues = List(
		name,
		fundName,
		donationDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
		location,
		amount.toString
	)
}
