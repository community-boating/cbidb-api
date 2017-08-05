package Storable.Fields

import Storable.{Filter, StorableObject}

class StringDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField(entity, fieldName) {
  def equalsConstant(c: String): Filter  =
    Filter(getFullyQualifiedName + " = '" + c + "'")
}