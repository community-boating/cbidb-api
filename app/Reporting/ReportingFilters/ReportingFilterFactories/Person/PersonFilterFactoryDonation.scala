package Reporting.ReportingFilters.ReportingFilterFactories.Person

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Entities.Entities.{Donation, Person}
import Reporting.ReportingFilters._
import Services.PersistenceBroker
import Services.ServerBootLoader.ssc

class PersonFilterFactoryDonation extends ReportingFilterFactory[Person] {
  val displayName: String = "Donated at least $X since Y"
  val argDefinitions = List(
    (ARG_DOUBLE, "0"), // donated amount
    (ARG_DATE, ssc.now.toLocalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
  )
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb

    type PersonID = Int

    val amount: Double = arg.split(",")(0).toDouble
    val sinceDate: LocalDate = LocalDate.parse(arg.split(",")(1), DateTimeFormatter.ofPattern("MM/dd/yyyy"))

    val donationsSinceDate: List[Donation] = pb.getObjectsByFilters(
      Donation,
      List(
        Donation.fields.donationDate.greaterEqualConstant(sinceDate)
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

    pb.getObjectsByIds(Person, personIDs).toSet
  })

}
