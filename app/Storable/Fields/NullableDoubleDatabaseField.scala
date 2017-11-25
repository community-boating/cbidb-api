package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class NullableDoubleDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Option[Double]](entity, persistenceFieldName) {
  def findValueInProtoStorable(row: ProtoStorable): Option[Option[Double]] = row.doubleFields.get(this.getRuntimeFieldName)

  def getFieldType(implicit pb: PersistenceBroker): String = pb match {
    case _: MysqlBroker => "decimal"
    case _: OracleBroker => "number"
  }

  def lessThanConstant(c: Double): Filter = {
    Filter(getFullyQualifiedName + " < " + c)
  }

  def inList(l: List[Double]): Filter =  {
    def groupIDs(ids: List[Double]): List[List[Double]] = {
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

  def equalsConstant(i: Option[Double]): Filter = i match {
    case Some(x: Double) => Filter(getFullyQualifiedName + " = " + i)
    case None => Filter(getFullyQualifiedName + " IS NULL")
  }

  def getValueFromString(s: String): Option[Option[Double]] = {
    if (s == "") Some(None)
    else {
      try {
        val d = s.toDouble
        Some(Some(d))
      } catch {
        case _: Throwable => None
      }
    }
  }
}
