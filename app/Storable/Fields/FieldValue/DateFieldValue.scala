package Storable.Fields.FieldValue

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.Fields.DateDatabaseField

class DateFieldValue(field: DateDatabaseField) extends FieldValue[LocalDate](field) {
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "'" + value.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
    case x if x == classOf[OracleBroker] => "TO_DATE('" + value.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
  }
}
