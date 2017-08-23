package Storable.Fields

import Services.PersistenceBroker
import Storable.{Filter, StorableObject}

abstract class DatabaseField(entity: StorableObject[_], fieldName: String) {
  def getFieldName: String = fieldName
  def getFullyQualifiedName: String = entity.entityName + "." + fieldName
  def getFieldType(pbClass: Class[_ <: PersistenceBroker]): String

  def isNull: Filter = Filter(getFullyQualifiedName + " IS NULL")
  def isNotNull: Filter = Filter(getFullyQualifiedName + " IS NOT NULL")
}
