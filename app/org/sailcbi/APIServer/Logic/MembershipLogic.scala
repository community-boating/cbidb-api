package org.sailcbi.APIServer.Logic

import java.time.LocalDate

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Entities.MagicIds

object MembershipLogic {
	def staggeredPaymentsAvailableNow(now: LocalDate): Boolean = {
		val month = now.getMonthValue
		month == 1 || month >= 9
	}

	def getMembershipStaggeredDownPayment: Currency = Currency.dollars(100)

	def getMembershipStaggeredPaymentsEndDate(now: LocalDate): LocalDate = {
		val FINAL_MONTH = 4
		val month = now.getMonthValue
		if (month < FINAL_MONTH) {
			LocalDate.of(now.getYear, FINAL_MONTH, 1)
		} else {
			LocalDate.of(now.getYear+1, FINAL_MONTH, 1)
		}

	}

	def getStaggeredPaymentOptions(startDate: LocalDate, endDate: LocalDate, fullPrice: Currency, downPayment: Currency): List[(LocalDate, Currency)] = {
		val dates = {
			def recurse(agg: List[LocalDate]): List[LocalDate] = {
				val nextMonth = startDate.plusMonths(agg.size+1)
				if (nextMonth.isEqual(endDate) || nextMonth.isAfter(endDate)) {
					agg
				} else {
					recurse(nextMonth :: agg)
				}
			}
			recurse(List.empty).reverse
		}
		val valueAfterDown = fullPrice - downPayment
		val payments = valueAfterDown.splitIntoPayments(dates.length)
		dates.zip(payments)
	}

	def membershipTypeAllowsStaggeredPayments(now: LocalDate, membershipTypeId: Int): Boolean = {
		membershipTypeId match {
			case
				MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_TYPE_ID
				| MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_PADDLING_TYPE_ID => staggeredPaymentsAvailableNow(now)
			case _ => false
		}
	}


}
