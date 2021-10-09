package org.sailcbi.APIServer.Reporting.ReportFactories

import com.coleji.neptune.Export.{ReportFactory, ReportingField, ReportingFilterFactory}
import com.coleji.neptune.Storable.StorableObject
import org.sailcbi.APIServer.Entities.EntityDefinitions.Donation
import org.sailcbi.APIServer.Reporting.ReportingFilterFactories.Donation.DonationFilterFactoryDateRange

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