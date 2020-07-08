package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class Check(val checkNumber: Option[String], val value: Currency, val school: Option[String], val checkName: Option[String]) extends RowData {
	def cellValues: List[String] = List(checkNumber.getOrElse("-"), value.format(), checkName.getOrElse(school.getOrElse("-")))
}
