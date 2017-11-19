package Reporting.ReportFactories

import Entities._
import Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance.JpClassInstanceFilterFactoryType
import Reporting.ReportingFilters.ReportingFilterFactories.Person.PersonFilterFactoryMemProgramYear
import Reporting.ReportingFilters.ReportingFilterFactory
import Reporting.{ReportFactory, ReportingField}
import Storable.StorableObject

class ReportFactoryPerson extends ReportFactory[Person] {
  val entityCompanion: StorableObject[Person] = Person

  def decorateInstancesWithParentReferences(instances: List[Person]): Unit = {}

  val fieldList: List[(String, ReportingField[Person])] = List(
    ("PersonId", ReportingField.getReportingFieldFromDatabaseField(Person.fields.personId, "Person ID", isDefault = true))
  )

  val filterList: List[(String, ReportingFilterFactory[Person])] = List(
    ("PersonFilterFactoryMemProgramYear", new PersonFilterFactoryMemProgramYear())
  )
}