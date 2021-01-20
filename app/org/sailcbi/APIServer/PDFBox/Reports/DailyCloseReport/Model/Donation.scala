package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

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
