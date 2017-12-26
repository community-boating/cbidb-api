package Storable.Fields.FieldValue

import Storable.Fields.DatabaseField
import Storable.StorableClass

abstract class FieldValue[T](instance: StorableClass, field: DatabaseField[T]) {
  private var value: Option[T] = None

  def set(v: T): Unit = {
    value = Some(v)
    instance.setDirty
  }
  def peek: Option[T] = value
  def get: T = value match {
    case Some(v) => v
    case None => throw new Exception("Attemted to get() an unset/unretrieved FieldValue " + instance.getCompanion.entityName + "." + field.getPersistenceFieldName)
  }
  def isSet: Boolean = value match {
    case Some(_) => true
    case None => false
  }

  def getPersistenceFieldName: String = field.getPersistenceFieldName
  def getPersistenceLiteral: String
  def getField: DatabaseField[T] = field
}
