package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Abstract.RowData

class ParkingData(
						 val open: Int,
						 val close: Int,
						 val plusMinus: Int,
						 val numberComp: Int,
						 val numberSold: Int,
						 val valueSold: Currency
				 ) extends RowData {
	val cellValues = List(
		open.toString,
		close.toString,
		plusMinus.toString,
		numberComp.toString,
		numberSold.toString,
		valueSold.format()
	)
}
