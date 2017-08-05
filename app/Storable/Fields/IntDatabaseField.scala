package Storable.Fields

import Storable.{Filter, StorableObject}

class IntDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField(entity, fieldName) {
  def lessThanConstant(c: Int): Filter = {
    Filter(getFullyQualifiedName + " < " + c)
  }

  def inList(l: List[Int]): Filter =  {
    if (l.isEmpty) Filter("")
    else Filter(getFullyQualifiedName + " in (" + l.mkString(", ") + ")")
  }
}