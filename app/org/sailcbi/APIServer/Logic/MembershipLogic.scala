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
	 * {@code priceFunction} takes a list of all the end dates, does a single expensive DB operation just once
	 * (e.g. checks each of them for renewal eligibilty in one query), then returns a function on a single end date to total price
	 */
	def calculateAllPaymentSchedules(startDate: LocalDate, priceFunction: List[LocalDate] => LocalDate => Currency, maxAddlMonths: Int = STAGGERED_PAYMENT_MAX_ADDL_MONTHS): List[List[(LocalDate, Currency)]] = {
		val endDates = (0 to maxAddlMonths).toList.map(startDate.plusMonths(_))
		// Call the DB, check renewal eligibility on each end date, keep the result as a hash for the next step
		val priceFunctionApplied = priceFunction(endDates)
		(0 to maxAddlMonths).toList.map(ct => calculatePaymentSchedule(startDate, priceFunctionApplied, ct))
	}

	def calculatePaymentSchedule(startDate: LocalDate, endDateToPrice: LocalDate => Currency, additionalMonths: Int): List[(LocalDate, Currency)] = {
		val dates = (0 to additionalMonths).map(startDate.plusMonths(_)).toList
		val endDate = dates.last
		val price = endDateToPrice(endDate)
		val payments = price.splitIntoPayments(dates.length)
		dates.zip(payments)
	}

	def membershipTypeAllowsStaggeredPayments(membershipTypeId: Int): Boolean = {
		membershipTypeId match {
			case MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_TYPE_ID
				 | MagicIds.MEMBERSHIP_TYPES.FULL_YEAR_PADDLING_TYPE_ID => true
			case _ => false
		}
	}
}
