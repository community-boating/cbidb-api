package org.sailcbi.APIServer.Logic

import java.time.LocalDate

import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.Entities.MagicIds

object MembershipLogic {
	/**
	 * What is the longest a staggered payment schedule can be stretched. Per Ying, they should never start in eg 2020 for a membership in 2022
	 * For simplicity, let's say if I start in December, why would I want to start later than next August
	 * 0 means lump payment, 1 means two payments (1 additional month) etc
	 */
	val STAGGERED_PAYMENT_MAX_ADDL_MONTHS = 8

	/**
	 * @return List of all possible payments schedules less than {@code maxAddlMonths}long
	 */
	def calculateAllPaymentSchedules(startDate: LocalDate, fullPrice: Currency, maxAddlMonths: Int = STAGGERED_PAYMENT_MAX_ADDL_MONTHS): List[List[(LocalDate, Currency)]] = {
		(0 to maxAddlMonths).toList.map(ct => calculatePaymentSchedule(startDate, fullPrice, ct))
	}

	def calculatePaymentSchedule(startDate: LocalDate, fullPrice: Currency, additionalMonths: Int): List[(LocalDate, Currency)] = {
		val dates = (0 to additionalMonths).map(startDate.plusMonths(_)).toList
		val payments = fullPrice.splitIntoPayments(dates.length)
		dates.zip(payments)
	}

	def membershipTypeAllowsStaggeredPayments(now: LocalDate, membershipTypeId: Int): Boolean = {
		membershipTypeId match {
			case MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_TYPE_ID
				 | MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_PADDLING_TYPE_ID => true
			case _ => false
		}
	}
}
