package Reporting.ReportingFilters.ReportingFilterFactories.Donation

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Entities.EntityDefinitions.Donation
import Reporting.ReportingFilters._
import Services.PersistenceBroker
import Services.ServerBootLoader.ssc

class DonationFilterFactoryDateRange extends ReportingFilterFactory[Donation] {
  val displayName: String = "Within Date Range"
  val argDefinitions = List(
    (ARG_DATE, ssc.now.toLocalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))),
    (ARG_DATE, ssc.now.toLocalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
  )
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Donation] = new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
    val split = arg.split(",")
    val start: LocalDate = LocalDate.parse(split(0), DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    val end: LocalDate = LocalDate.parse(split(1), DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    pb.getObjectsByFilters(
      Donation,
      List(
        Donation.fields.donationDate.greaterEqualConstant(start),
        Donation.fields.donationDate.lessEqualConstant(end)
      )
    ).toSet
  })
}