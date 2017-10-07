package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.DatabaseField

abstract class FieldValue[T](field: DatabaseField[T]) {
  var value: Option[T] = None

  def set(v: T): Unit = {
    value = Some(v)
  }
  def peek: Option[T] = value
  def get: T = value match {
    case None => throw new Exception("Attemted to get() an unretrieved FieldValue")
    case Some(v: T) => v
  }

  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String
}
