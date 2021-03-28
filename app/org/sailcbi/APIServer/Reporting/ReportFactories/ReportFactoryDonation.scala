package org.sailcbi.APIServer.Reporting.ReportFactories

import com.coleji.framework.Storable.StorableObject
import org.sailcbi.APIServer.Entities.EntityDefinitions.Donation
import org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.Donation.DonationFilterFactoryDateRange
import org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactory
import org.sailcbi.APIServer.Reporting.{ReportFactory, ReportingField}

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