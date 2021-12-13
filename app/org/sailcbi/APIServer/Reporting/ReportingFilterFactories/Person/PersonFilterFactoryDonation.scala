package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.Person

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export._
import org.sailcbi.APIServer.Entities.EntityDefinitions.{Donation, Person}
import org.sailcbi.APIServer.Logic.DateLogic

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PersonFilterFactoryDonation extends ReportingFilterFactory[Person] {
	val displayName: String = "Donated at least $X since Y"
	val argDefinitions = List(
		(ARG_DOUBLE, "0"), // donated amount
		(ARG_DATE, DateLogic.now.toLocalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		implicit val rc: UnlockedRequestCache = _rc

		type PersonID = Int

		val amount: Double = arg.split(",")(0).toDouble
		val sinceDate: LocalDate = LocalDate.parse(arg.split(",")(1), DateTimeFormatter.ofPattern("MM/dd/yyyy"))

		val donationsSinceDate: List[Donation] = rc.getObjectsByFilters(
			Donation,
			List(
				Donation.fields.donationDate.alias.greaterEqualConstant(sinceDate)
			)
		)

		println("found " + donationsSinceDate.length + " donations")

		val byPersonId: Map[Int, List[Donation]] = donationsSinceDate.groupBy(_.values.personId.get)

		val personIDs = byPersonId.filter(m => {
			m._2.foldLeft(0: Double)((sum, d) => {
				val donationAmount = d.values.amount.get match {
					case None => 0
					case Some(d) => d
				}
				sum + donationAmount
			}) >= amount
		}).keys.toList

		rc.getObjectsByIds(Person, personIDs).toSet
	})

}
