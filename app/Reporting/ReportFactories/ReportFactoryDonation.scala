package Reporting.ReportFactories

import Entities.Donation
import Reporting.ReportingFilters.ReportingFilterFactories.Donation.DonationFilterFactoryDateRange
import Reporting.ReportingFilters.ReportingFilterFactory
import Reporting.{ReportFactory, ReportingField}
import Storable.StorableObject

class ReportFactoryDonation extends ReportFactory[Donation] {
  val entityCompanion: StorableObject[Donation] = Donation

  def decorateInstancesWithParentReferences(instances: List[Donation]): Unit = {}

  val fieldList: List[(String, ReportingField[Donation])] = List(
    ("DonationId", ReportingField.getReportingFieldFromDatabaseField(Donation.fields.donationId, "Donation ID", isDefault = true)),
    ("DonationDate", ReportingField.getReportingFieldFromDatabaseField(Donation.fields.donationDate, "Donation Date", isDefault = true)),
    ("Amount", ReportingField.getReportingFieldFromDatabaseField(Donation.fields.amount, "Amount", isDefault = true))
  )

  val filterList: List[(String, ReportingFilterFactory[Donation])] = List(
    ("DonationFilterFactoryDateRange", new DonationFilterFactoryDateRange),
  )
}