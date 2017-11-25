package Reporting.ReportFactories

import Entities._
import Reporting.ReportingFilters.ReportingFilterFactories.Person._
import Reporting.ReportingFilters.ReportingFilterFactory
import Reporting.{ReportFactory, ReportingField}
import Storable.StorableObject

class ReportFactoryPerson extends ReportFactory[Person] {
  val entityCompanion: StorableObject[Person] = Person

  def decorateInstancesWithParentReferences(instances: List[Person]): Unit = {}

  val fieldList: List[(String, ReportingField[Person])] = List(
    ("PersonId", ReportingField.getReportingFieldFromDatabaseField(Person.fields.personId, "Person ID", isDefault = true)),
    ("NameFirst", ReportingField.getReportingFieldFromDatabaseField(Person.fields.nameFirst, "First Name", isDefault = true)),
    ("NameLast", ReportingField.getReportingFieldFromDatabaseField(Person.fields.nameLast, "Last Name", isDefault = true)),
    ("Email", ReportingField.getReportingFieldFromDatabaseField(Person.fields.email, "Email", isDefault = true))
  )

  val filterList: List[(String, ReportingFilterFactory[Person])] = List(
    ("PersonFilterFactoryMemProgramYear", new PersonFilterFactoryMemProgramYear),
    ("PersonFilterFactoryRating", new PersonFilterFactoryRating),
    ("PersonFilterFactoryTag", new PersonFilterFactoryTag),
    ("PersonFilterFactoryJpParentSeason", new PersonFilterFactoryJpParentSeason),
    ("PersonFilterFactoryDonation", new PersonFilterFactoryDonation)
  )
}