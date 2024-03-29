package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Util.Currency
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonMembership
import org.sailcbi.APIServer.Entities.MagicIds

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Month}

object MembershipLogic {
	/**
	 * What is the longest a staggered payment schedule can be stretched. Per Ying, they should never start in eg 2020 for a membership in 2022
	 * For simplicity, let's say if I start in December, why would I want to start later than next August
	 * 0 means lump payment, 1 means two payments (1 additional month) etc
	 */
	val STAGGERED_PAYMENT_MAX_ADDL_MONTHS = 6

	/**
	 * @return List of all possible payments schedules less than {@code maxAddlMonths}long
	 * {@code priceFunction} takes a list of all the end dates, does a single expensive DB operation just once
	 * (e.g. checks each of them for renewal eligibilty in one query), then returns a function on a single end date to total price
	 */
	def calculateAllPaymentSchedules(startDate: LocalDate, priceFunction: List[LocalDate] => LocalDate => Currency, maxAddlMonths: Int = STAGGERED_PAYMENT_MAX_ADDL_MONTHS): List[List[(LocalDate, Currency)]] = {
		val countAndEndDates = (0 to maxAddlMonths).toList.map(ct => (ct, startDate.plusMonths(ct)))
			.filter(countAndEndDate => {
				val endDate = countAndEndDate._2
				val month = endDate.format(DateTimeFormatter.ofPattern("MM")).toInt
				endDate.equals(startDate) || month > 10 || month < 6
			})
		// Call the DB, check renewal eligibility on each end date, keep the result as a hash for the next step
		val priceFunctionApplied = priceFunction(countAndEndDates.map(_._2))
		countAndEndDates.map(countAndEndDate => calculatePaymentSchedule(startDate, priceFunctionApplied, countAndEndDate._1))
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

	def memStartDateFromPurchaseDate(purchaseDate: LocalDate): LocalDate = {
		val purchaseMonth = purchaseDate.getMonthValue // 1 - 12
		purchaseDate.getMonth match {
			case Month.NOVEMBER | Month.DECEMBER => purchaseDate.withDayOfMonth(1).plusMonths(16 - purchaseMonth)
			case Month.JANUARY | Month.FEBRUARY | Month.MARCH => purchaseDate.withDayOfMonth(1).plusMonths(4 - purchaseMonth)
			case _ => purchaseDate
		}
	}

	def isActive(pm: PersonMembership, now: LocalDate): Boolean = {
		val start = pm.values.startDate.get
		val exp = pm.values.expirationDate.get

		(start.isEmpty || !start.get.isAfter(now)) &&
			(exp.isEmpty || !exp.get.isBefore(now))
	}
}
