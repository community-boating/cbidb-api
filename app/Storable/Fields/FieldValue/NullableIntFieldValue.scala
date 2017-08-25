package Storable.Fields.FieldValue

import Storable.Fields.IntDatabaseField

case class NullableIntFieldValue(field: IntDatabaseField, value: Option[Int]) extends FieldValue {
  def getFieldName: String = field.getFieldName
  def getInsertValue: String = value match {
    case Some(x) => x.toString
    case None => "NULL"
  }
}