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
