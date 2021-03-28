package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import com.coleji.framework.PDFBox.Abstract.RowData

class AR(val sourceName: String, val value: Currency) extends RowData {
	def cellValues: List[String] = List(sourceName, value.format())
}
