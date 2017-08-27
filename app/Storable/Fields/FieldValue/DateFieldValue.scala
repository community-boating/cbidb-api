package Storable.Fields.FieldValue

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.Fields.DateDatabaseField

case class DateFieldValue(field: DateDatabaseField, value: LocalDate) extends FieldValue{
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "'" + value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
    case x if x == classOf[OracleBroker] => "TO_DATE('" + value.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
  }
}
