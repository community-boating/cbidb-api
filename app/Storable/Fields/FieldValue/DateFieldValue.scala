package Storable.Fields.FieldValue

import java.time.LocalDate

import Storable.Fields.DateDatabaseField

case class DateFieldValue(field: DateDatabaseField, value: LocalDate) extends FieldValue{

}
