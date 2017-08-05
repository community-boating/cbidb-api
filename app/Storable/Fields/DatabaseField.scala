package Storable.Fields

import Storable.{Filter, StorableObject}

abstract class DatabaseField(entity: StorableObject[_], fieldName: String) {
  def getFieldName: String = fieldName
  def getFullyQualifiedName: String = entity.entityName + "." + fieldName

  def isNull: Filter = Filter(getFullyQualifiedName + " IS NULL")
  def isNotNull: Filter = Filter(getFullyQualifiedName + " IS NOT NULL")
}
