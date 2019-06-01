package CbiUtil

import java.text.DecimalFormat

object CurrencyFormat {
	private def roundDoubleToTwoDecimals(d: Double, commas: Boolean): String = {
		val comma = if (commas) "," else ""
		new DecimalFormat("##" + comma + "##0.00").format(d)
	}

	def withCents(d: Double, commas: Boolean = true): String = "$" + roundDoubleToTwoDecimals(d, commas)

	def int(i: Int, commas: Boolean = true, addZeroes: Boolean = false): String = {
		val comma = if (commas) "," else ""
		val format = new DecimalFormat("##" + comma + "##0")
		"$" + format.format(i) + (if (addZeroes) ".00" else "")
	}
}
