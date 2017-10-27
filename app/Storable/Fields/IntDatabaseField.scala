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

  def inList(l: List[Int]): Filter = {
    def groupIDs(ids: List[Int]): List[List[Int]] = {
      val MAX_IDS = 900
      if (ids.length <= MAX_IDS) List(ids)
      else {
        val splitList = ids.splitAt(MAX_IDS)
        splitList._1 :: groupIDs(splitList._2)
      }
    }

    if (l.isEmpty) Filter("")
    else Filter(groupIDs(l).map(group => {
      getFullyQualifiedName + " in (" + group.mkString(", ") + ")"
    }).mkString(" OR "))
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
