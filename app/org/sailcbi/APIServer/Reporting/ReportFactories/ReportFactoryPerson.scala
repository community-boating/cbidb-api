package org.sailcbi.APIServer.Reporting.ReportFactories

import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.Person._
import org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactory
import org.sailcbi.APIServer.Reporting.{ReportFactory, ReportingField}
import org.sailcbi.APIServer.Storable.StorableObject

class ReportFactoryPerson extends ReportFactory[Person] {
	val entityCompanion: StorableObject[Person] = Person

	def decorateInstancesWithParentReferences(instances: List[Person]): Unit = {}

	val fieldList: List[(String, ReportingField[Person])] = List(
		("PersonId", ReportingField.getReportingFieldFromDatabaseField(Person.fields.personId, "Person ID", isDefault = true)),
		("NameFirst", ReportingField.getReportingFieldFromDatabaseField(Person.fields.nameFirst, "First Name", isDefault = true)),
		("NameLast", ReportingField.getReportingFieldFromDatabaseField(Person.fields.nameLast, "Last Name", isDefault = true)),
		("Email", ReportingField.getReportingFieldFromDatabaseField(Person.fields.email, "Email", isDefault = true)),
		("Addr_1", ReportingField.getReportingFieldFromDatabaseField(Person.fields.addr1, "Addr 1", isDefault = false)),
		("Addr_2", ReportingField.getReportingFieldFromDatabaseField(Person.fields.addr2, "Addr 2", isDefault = false)),
		("Addr_3", ReportingField.getReportingFieldFromDatabaseField(Person.fields.addr3, "Addr 3", isDefault = false)),
		("City", ReportingField.getReportingFieldFromDatabaseField(Person.fields.city, "City", isDefault = false)),
		("State", ReportingField.getReportingFieldFromDatabaseField(Person.fields.state, "State", isDefault = false)),
		("Zip", ReportingField.getReportingFieldFromDatabaseField(Person.fields.zip, "Zip", isDefault = false))
	)

	val filterList: List[(String, ReportingFilterFactory[Person])] = List(
		("PersonFilterFactoryMemProgramYear", new PersonFilterFactoryMemProgramYear),
		("PersonFilterFactoryRating", new PersonFilterFactoryRating),
		("PersonFilterFactoryTag", new PersonFilterFactoryTag),
		("PersonFilterFactoryJpParentSeason", new PersonFilterFactoryJpParentSeason),
		("PersonFilterFactoryDonation", new PersonFilterFactoryDonation)
	)
}