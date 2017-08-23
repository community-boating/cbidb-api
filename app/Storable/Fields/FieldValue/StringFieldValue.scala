package Storable.Fields.FieldValue

import Storable.Fields.StringDatabaseField

case class StringFieldValue(field: StringDatabaseField, value: String) extends FieldValue {

}
