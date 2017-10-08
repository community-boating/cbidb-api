package Storable.Fields

import Services.PersistenceBroker
import Storable.{ProtoStorable, Filter, StorableObject}

abstract class DatabaseField[T](entity: StorableObject[_], fieldName: String) {
  def getFieldName: String = fieldName
  def getFullyQualifiedName: String = entity.entityName + "." + fieldName
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String

  def getValue(row: ProtoStorable): T

  def isNull: Filter = Filter(getFullyQualifiedName + " IS NULL")
  def isNotNull: Filter = Filter(getFullyQualifiedName + " IS NOT NULL")
}
