package com.coleji.neptune.Util

import com.coleji.neptune.Util.Currency.currencyAsNumeric

class Currency(val cents: Int) {
	override def equals(obj: Any): Boolean = obj match {
		case c: Currency => this.cents == c.cents
		case _ => false
	}
	def +(that: Currency): Currency = currencyAsNumeric.plus(this, that)

	def -(that: Currency): Currency = currencyAsNumeric.minus(this, that)

	/**
	 * Suppose you have e.g. $10 and you want to split that into three "equal" payments
	 * This would return List($3.34, $3.34, $3.32).
	 * At the time of writing the only usecase for this is to determine monthly membership payments,
	 * and I want the values to be scrictly decreasing. If there are other usecases that require different handling of uneven divisions,
	 * add a {@code splitType} enum parameter
	 * @return
	 */
	def splitIntoPayments(ct: Int): List[Currency] = {
		val singlePaymentCents = (this.cents.toDouble / ct).ceil.toInt
		val finalPaymentCents = this.cents - (singlePaymentCents * (ct-1))
		List.range(1, ct).map(_ => Currency.cents(singlePaymentCents)) ::: List(Currency.cents(finalPaymentCents))
	}

	def format(commas: Boolean = true, zeroes: Boolean = true): String = {
		val allDigits = Math.abs(this.cents).toString
		val dollarsAndCents = {
			if (allDigits.length == 1) ("0", "0" + allDigits)
			else if (allDigits.length == 2) ("0", allDigits)
			else (allDigits.take(allDigits.length - 2), allDigits.drop(allDigits.length - 2))
		}
		val dollarsString = dollarsAndCents._1
		val centsString = dollarsAndCents._2
		val formattedDollars: String = if (!commas) dollarsString else {
			def recurse(cs: List[Char]): String = {
				if (cs.length < 4) cs.mkString("")
				else cs.take(cs.length - 3).mkString("") + "," + recurse(cs.drop(cs.length - 3))
			}

			recurse(dollarsString.toCharArray.toList)
		}
		val formattedCents = if (!zeroes && centsString == "00") "" else "." + centsString
		(if (this.cents < 0) "(-)" else "") + "$" + formattedDollars + formattedCents
	}

	def formatSpecialIfZero(ifZero: String, commas: Boolean = true, zeroes: Boolean = true): String =
		if (cents == 0) ifZero else this.format(commas, zeroes)

	override def toString: String = this.format()
}

object Currency {
	def dollars(i: Int): Currency = new Currency(i * 100)

	def dollars(d: Double): Currency = new Currency(Currency.toCents(d))

	def cents(i: Int): Currency = new Currency(i)

	def toCents(d: Double): Int = Math.round(d * 100d).toInt

	implicit object currencyAsNumeric extends Numeric[Currency] {
		override def plus(x: Currency, y: Currency): Currency = Currency.cents(x.cents + y.cents)

		override def minus(x: Currency, y: Currency): Currency = Currency.cents(x.cents - y.cents)

		override def times(x: Currency, y: Currency): Currency = Currency.cents(x.cents * y.cents)

		override def negate(x: Currency): Currency = Currency.cents(-1 * x.cents)

		override def fromInt(x: Int): Currency = Currency.cents(x)

		override def toInt(x: Currency): Int = x.cents

		override def toLong(x: Currency): Long = x.cents.toLong

		override def toFloat(x: Currency): Float = x.cents.toFloat

		override def toDouble(x: Currency): Double = x.cents.toDouble

		override def compare(x: Currency, y: Currency): Int = if (x.cents > y.cents) 1 else if (x.cents < y.cents) -1 else 0
	}

}