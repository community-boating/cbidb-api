package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class IntDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Int](entity, persistenceFieldName) {
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "integer"
    case x if x == classOf[OracleBroker] => "number"
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[Int] = {
    row.intFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null Int field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def lessThanConstant(c: Int): Filter = {
    Filter(getFullyQualifiedName + " < " + c)
  }

  def inList(l: List[Int]): Filter =  {
    if (l.isEmpty) Filter("")
    else Filter(getFullyQualifiedName + " in (" + l.mkString(", ") + ")")
  }

  def equalsConstant(i: Int): Filter = Filter(getFullyQualifiedName + " = " + i)

  def getValueFromString(s: String): Option[Int] = {
    try {
      val d = s.toInt
      Some(d)
    } catch {
      case _ => None
    }
  }
}
