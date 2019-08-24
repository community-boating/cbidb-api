package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class TaxablesItem(
						  val closeId: Int,
						  val itemName: String,
						  val discountName: Option[String],
						  val count: Int,
						  val pretax: Currency,
						  val taxAmount: Currency
				  ) extends RowData {
	def cellValues: List[String] = List(
		itemName,
		discountName.getOrElse(""),
		count.toString,
		pretax.format(),
		taxAmount.format(),
		(pretax + taxAmount).format()
	)
}