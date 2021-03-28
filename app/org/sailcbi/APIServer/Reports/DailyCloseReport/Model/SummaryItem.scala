package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.framework.PDFBox.Abstract.RowData
import org.sailcbi.APIServer.CbiUtil.Currency

class SummaryItem(
						 val itemName: String,
						 val discountName: Option[String],
						 val inpersonCount: Option[Int],
						 val inpersonTotal: Option[String],
						 val onlineCount: Option[Int],
						 val onlineTotal: Option[Currency]
				 ) extends RowData {
	def cellValues: List[String] = List(
		itemName,
		discountName.getOrElse(""),
		inpersonCount.getOrElse("").toString,
		inpersonTotal match { case Some(c) => c; case None => "" },
		onlineCount.getOrElse("").toString,
		onlineTotal match { case Some(c) => c.format(); case None => "" }
	)
}