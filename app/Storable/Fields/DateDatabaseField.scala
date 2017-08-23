package Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, StorableObject}

class DateDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField(entity, fieldName) {
  def getFieldType(pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "date"
    case x if x == classOf[OracleBroker] => "date"
  }

  def isYearConstant(year: Int): Filter = Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
  def isDateConstant(date: LocalDate): Filter =
    Filter("TRUNC(" + getFullyQualifiedName + ") = TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
}