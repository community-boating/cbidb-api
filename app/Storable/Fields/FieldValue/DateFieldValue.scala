package Storable.Fields.FieldValue

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Storable.Fields.DateDatabaseField

case class DateFieldValue(field: DateDatabaseField, value: LocalDate) extends FieldValue{
  val format: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
  def getFieldName: String = field.getFieldName
  def getInsertValue: String = "TO_DATE('" + value.format(format) + "', 'MM/DD/YYYY')"
}
