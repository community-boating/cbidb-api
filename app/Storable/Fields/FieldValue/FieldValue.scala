package Storable.Fields.FieldValue

import Services.PersistenceBroker

abstract class FieldValue[T] {
  var value: Option[T] = None

  def set(v: T): Unit = {
    value = Some(v)
  }
  def peek: Option[T] = value
  def get: T = value match {
    case None => throw new Exception("Attemted to get() an unretrieved FieldValue")
    case Some(v: T) => v
  }

  def getFieldName: String
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String
}
