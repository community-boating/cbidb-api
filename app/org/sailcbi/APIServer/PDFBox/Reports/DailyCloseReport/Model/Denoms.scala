package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.DailyCloseReport.CloseCashResult

// value in each denom
case class Denoms(
						 d100: Currency,
						 d50: Currency,
						 d20: Currency,
						 d10: Currency,
						 d5: Currency,
						 d2: Currency,
						 d1: Currency,
						 c100: Currency,
						 c50: Currency,
						 c25: Currency,
						 c10: Currency,
						 c5: Currency,
						 c1: Currency
				 ) {
	/*def asInt(v: Int): String = if(v==0) "" else CurrencyFormat.int(v)
	def asDouble(v: Double): String = if(v==0) "" else CurrencyFormat.withCents(v)
	*/
	val ifZero = "-"

	def asListOfList: List[List[String]] = List(
		List("$100", d100.formatSpecialIfZero(ifZero)),
		List("$50", d50.formatSpecialIfZero(ifZero)),
		List("$20", d20.formatSpecialIfZero(ifZero)),
		List("$10", d10.formatSpecialIfZero(ifZero)),
		List("$5", d5.formatSpecialIfZero(ifZero)),
		List("$2", d2.formatSpecialIfZero(ifZero)),
		List("$1", d1.formatSpecialIfZero(ifZero)),
		List("$1.00", c100.formatSpecialIfZero(ifZero)),
		List("$0.50", c50.formatSpecialIfZero(ifZero)),
		List("$0.25", c25.formatSpecialIfZero(ifZero)),
		List("$0.10", c10.formatSpecialIfZero(ifZero)),
		List("$0.05", c5.formatSpecialIfZero(ifZero)),
		List("$0.01", c1.formatSpecialIfZero(ifZero))
	)

	def total: Currency = d100 + d50 + d20 + d10 + d5 + d2 + d1 + c100 + c50 + c25 + c10 + c5 + c1
}

object Denoms {
	def getDenoms(data: List[CloseCashResult], locationShort: String): Denoms = {
		val filteredData = data.filter(_.locationShort == locationShort)
		Denoms(
			filteredData.filter(_.denominationCents == 10000).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 5000).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 2000).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 1000).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 500).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 200).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(d => d.denominationCents == 100 && d.isBill).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(d => d.denominationCents == 100 && !d.isBill).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 50).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 25).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 10).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 5).map(_.value).headOption.getOrElse(Currency.cents(0)),
			filteredData.filter(_.denominationCents == 1).map(_.value).headOption.getOrElse(Currency.cents(0))
		)
	}
}
