package org.sailcbi.APIServer.Logic

import java.time.LocalDate

import org.sailcbi.APIServer.CbiUtil.Currency

object MembershipLogic {
	def getStaggeredPaymentOptions(startDate: LocalDate, endDate: LocalDate, fullPrice: Currency, downPayment: Currency): List[(LocalDate, Currency)] = {
		val dates = {
			def recurse(currentDate: LocalDate, agg: List[LocalDate]): List[LocalDate] = {
				val nextMonth = startDate.plusMonths(agg.size+1)
				if (nextMonth.isEqual(endDate) || nextMonth.isAfter(endDate)) {
					agg
				} else {
					recurse(nextMonth, nextMonth :: agg)
				}
			}
			recurse(startDate, List.empty).reverse
		}
		val valueAfterDown = fullPrice - downPayment
		val payments = valueAfterDown.splitIntoPayments(dates.length)
		dates.zip(payments)
	}
}
