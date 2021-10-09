package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Currency

class RetailData(
						val itemName: String,
						val numberSold: Int,
						val discounts: Currency,
						val pretax: Currency,
						val taxAmount: Currency,
						val totalAmount: Currency,
						val numComp: Int,
						val amountComp: Currency
				) extends RowData {
	val cellValues = List(
		itemName,
		numberSold.toString,
		discounts.format(),
		pretax.format(),
		taxAmount.format(),
		totalAmount.format(),
		numComp.toString,
		amountComp.format()
	)
}
