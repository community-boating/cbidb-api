package Storable.Fields.FieldValue

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import Storable.Fields.DateTimeDatabaseField

case class DateTimeFieldValue(field: DateTimeDatabaseField, value: LocalDateTime) extends FieldValue {
  val format: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ssa")
  def getFieldName: String = field.getFieldName
  def getInsertValue: String = "TO_DATE('" + value.format(format) + "', 'MM/DD/YYYY HH:MI:SSPM')"
}
