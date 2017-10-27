package Storable.Fields

import Services.PersistenceBroker
import Storable.{Filter, ProtoStorable, StorableObject}

abstract class DatabaseField[T](entity: StorableObject[_], persistenceFieldName: String) {
  def getPersistenceFieldName: String = persistenceFieldName
  def getFullyQualifiedName: String = entity.entityName + "." + persistenceFieldName
  def getFieldType(implicit pb: PersistenceBroker): String

  private var runtimeFieldName: Option[String] = None
  def getRuntimeFieldName: String = runtimeFieldName match {
    case None => throw new Exception("Runtime field name never got set for " + entity.entityName + "." + persistenceFieldName)
    case Some(s: String) => s
  }
  def setRuntimeFieldName(s: String): Unit = runtimeFieldName match {
    case Some(_) => throw new Exception("Multiple calls to set runtimeFieldName for " + entity.entityName + "." + persistenceFieldName)
    case None => runtimeFieldName = Some(s)
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[T]

  def isNull: Filter = Filter(getFullyQualifiedName + " IS NULL")
  def isNotNull: Filter = Filter(getFullyQualifiedName + " IS NOT NULL")

  def getValueFromString(s: String): Option[T]
}
