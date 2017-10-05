package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.IntDatabaseField

class NullableIntFieldValue(field: IntDatabaseField) extends FieldValue[Option[Int]] {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = value.get match {
    case Some(x) => x.toString
    case None => "NULL"
  }
}