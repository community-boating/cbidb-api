package Storable.Fields.FieldValue

import Storable.Fields.IntDatabaseField

case class NullableIntFieldValue(field: IntDatabaseField, value: Option[Int]) extends FieldValue {

}