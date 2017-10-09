package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class NullableIntDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Option[Int]](entity, persistenceFieldName) {
  def findValueInProtoStorable(row: ProtoStorable): Option[Option[Int]] = row.intFields.get(this.getRuntimeFieldName)

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

  def getValueFromString(s: String): Option[Option[Int]] = {
    if (s == "") Some(None)
    else {
      try {
        val d = s.toInt
        Some(Some(d))
      } catch {
        case _ => None
      }
    }
  }
}
