package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import com.coleji.framework.Util.Currency

class AR(val sourceName: String, val value: Currency) extends RowData {
	def cellValues: List[String] = List(sourceName, value.format())
}
