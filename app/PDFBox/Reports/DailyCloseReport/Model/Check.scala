package PDFBox.Reports.DailyCloseReport.Model

import CbiUtil.Currency
import PDFBox.Abstract.RowData

class Check(val checkNumber: Option[String], val value: Currency, val school: Option[String]) extends RowData {
	def cellValues: List[String] = List(checkNumber.getOrElse("-"), value.format(), school.getOrElse("-"))
}
