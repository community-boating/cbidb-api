package Storable.Fields.FieldValue

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.Fields.DateTimeDatabaseField

class DateTimeFieldValue(field: DateTimeDatabaseField) extends FieldValue[LocalDateTime] {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "'" + value.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'"
    case x if x == classOf[OracleBroker] => "TO_DATE('" + value.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH:MI:SS')"
  }
}
