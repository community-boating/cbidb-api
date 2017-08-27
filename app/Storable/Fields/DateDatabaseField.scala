package Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, StorableObject}

class DateDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField(entity, fieldName) {
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "date"
    case x if x == classOf[OracleBroker] => "date"
  }

  def isYearConstant(year: Int)(implicit pbClass: Class[_ <: PersistenceBroker]): Filter = pbClass match {
    case x if x == classOf[MysqlBroker] => {
      val jan1 = LocalDate.of(year, 1, 1)
      val nextJan1 = LocalDate.of(year+1, 1, 1)
      val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      Filter(getFullyQualifiedName + ">= " + jan1.format(pattern) + " AND " + getFullyQualifiedName + " < " + nextJan1.format(pattern))
    }
    case x if x == classOf[OracleBroker] => Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
  }

  def isDateConstant(date: LocalDate)(implicit pbClass: Class[_ <: PersistenceBroker]): Filter = pbClass match {
    case x if x == classOf[MysqlBroker] =>
      Filter(getFullyQualifiedName + " = " + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    case x if x == classOf[OracleBroker] =>
      Filter("TRUNC(" + getFullyQualifiedName + ") = TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
  }
}