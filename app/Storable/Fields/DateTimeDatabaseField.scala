package Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, StorableObject}

class DateTimeDatabaseField(entity: StorableObject[_], fieldName: String) extends DateDatabaseField(entity, fieldName) {
  override def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "datetime"
    case x if x == classOf[OracleBroker] => "date"
  }

  override def isDateConstant(date: LocalDate)(implicit pbClass: Class[_ <: PersistenceBroker]): Filter = pbClass match {
    case x if x == classOf[MysqlBroker] =>
      Filter(
        getFullyQualifiedName + " >= '" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'" + " AND " +
        getFullyQualifiedName + " < '" + date.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
      )
    case x if x == classOf[OracleBroker] => super.isDateConstant(date)
  }
}