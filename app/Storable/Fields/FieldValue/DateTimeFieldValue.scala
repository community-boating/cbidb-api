package Storable.Fields.FieldValue

import java.time.LocalDateTime

import Storable.Fields.DateTimeDatabaseField

case class DateTimeFieldValue(field: DateTimeDatabaseField, value: LocalDateTime) extends FieldValue {

}
