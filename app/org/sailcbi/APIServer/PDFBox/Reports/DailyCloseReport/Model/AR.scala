package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class AR(val sourceName: String, val value: Currency) extends RowData {
	def cellValues: List[String] = List(sourceName, value.format())
}
