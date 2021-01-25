package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.Donation

import org.sailcbi.APIServer.Entities.EntityDefinitions.Donation
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.RequestCache

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DonationFilterFactoryDateRange extends ReportingFilterFactory[Donation] {
	val displayName: String = "Within Date Range"
	val argDefinitions = List(
		(ARG_DATE, DateLogic.now.toLocalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))),
		(ARG_DATE, DateLogic.now.toLocalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
	)

	def getFilter(rc: RequestCache, arg: String): ReportingFilter[Donation] = new ReportingFilterFunction(rc, (rc: RequestCache) => {
		val split = arg.split(",")
		val start: LocalDate = LocalDate.parse(split(0), DateTimeFormatter.ofPattern("MM/dd/yyyy"))
		val end: LocalDate = LocalDate.parse(split(1), DateTimeFormatter.ofPattern("MM/dd/yyyy"))

		rc.getObjectsByFilters(
			Donation,
			List(
				Donation.fields.donationDate.greaterEqualConstant(start),
				Donation.fields.donationDate.lessEqualConstant(end)
			)
		).toSet
	})
}