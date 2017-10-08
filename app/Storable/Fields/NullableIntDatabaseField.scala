package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{DatabaseRow, Filter, StorableObject}

class NullableIntDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField[Option[Int]](entity, fieldName) {
  def getValue(row: DatabaseRow): Option[Int] = {
    row.intFields.get(fieldName) match {
      case Some(Some(x: Int)) => Some(x)
      case Some(None) => None
      case None => throw new Exception("Nullable Int did not exist in DatabaseRow")
    }
  }

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "integer"
    case x if x == classOf[OracleBroker] => "number"
  }

  def lessThanConstant(c: Int): Filter = {
    Filter(getFullyQualifiedName + " < " + c)
  }

  def inList(l: List[Int]): Filter =  {
    if (l.isEmpty) Filter("")
    else Filter(getFullyQualifiedName + " in (" + l.mkString(", ") + ")")
  }

  def equalsConstant(i: Option[Int]): Filter = i match {
    case Some(x: Int) => Filter(getFullyQualifiedName + " = " + i)
    case None => Filter(getFullyQualifiedName + " IS NULL")
  }
}
