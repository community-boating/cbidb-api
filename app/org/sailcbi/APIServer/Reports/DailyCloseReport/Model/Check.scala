package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.{Currency, DateUtil}
import com.coleji.framework.PDFBox.Abstract.RowData

import java.time.LocalDate

class Check(val checkNumber: Option[String], val value: Currency, val school: Option[String], val checkName: Option[String], val checkDate: Option[LocalDate]) extends RowData {
	def cellValues: List[String] = List(
		checkNumber.getOrElse("-"),
		value.format(),
		checkName.getOrElse(school.getOrElse("-")),
		checkDate.map(_.format(DateUtil.DATE_FORMATTER)).getOrElse("-")
	)
}
