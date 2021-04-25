package org.sailcbi.APIServer.Logic

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import java.time.LocalDate

@RunWith(classOf[JUnitRunner])
class MembershipLogicTest extends FunSuite {
	test("start dates from purchase dates") {
		val year = 2021

		val purchaseDates = (1 to 12).toList.map(month => LocalDate.of(year, month, 1))
		val startDates = purchaseDates.map(MembershipLogic.memStartDateFromPurchaseDate)
		println(purchaseDates)
		assert(startDates == List(
			LocalDate.of(year, 4, 1),
			LocalDate.of(year, 4, 1),
			LocalDate.of(year, 4, 1),
			LocalDate.of(year, 4, 1),
			LocalDate.of(year, 5, 1),
			LocalDate.of(year, 6, 1),
			LocalDate.of(year, 7, 1),
			LocalDate.of(year, 8, 1),
			LocalDate.of(year, 9, 1),
			LocalDate.of(year, 10, 1),
			LocalDate.of(year+1, 4, 1),
			LocalDate.of(year+1, 4, 1)
		))
	}
}
