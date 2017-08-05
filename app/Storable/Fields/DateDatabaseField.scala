package Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Storable.{Filter, StorableObject}

class DateDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField(entity, fieldName) {
  def isYearConstant(year: Int): Filter = Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
  def isDateConstant(date: LocalDate): Filter =
    Filter("TRUNC(" + getFullyQualifiedName + ") = TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
}