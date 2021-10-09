package org.sailcbi.APIServer.Reports.DailyCloseReport.Model

import com.coleji.neptune.PDFBox.Abstract.RowData
import com.coleji.neptune.Util.Currency

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
