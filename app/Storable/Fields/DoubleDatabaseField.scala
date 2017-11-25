package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker, RelationalBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class DoubleDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Double](entity, persistenceFieldName) {
  def getFieldType(implicit pb: PersistenceBroker): String = pb match {
    case _: MysqlBroker => "decimal"
    case _: OracleBroker => "number"
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[Double] = {
    row.doubleFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null Double field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def lessThanConstant(c: Double): Filter = {
    Filter(getFullyQualifiedName + " < " + c)
  }

  def inList(l: List[Double])(implicit pb: PersistenceBroker): Filter = pb match {
    case rl: RelationalBroker => {
      def groupIDs(ids: List[Double]): List[List[Double]] = {
        if (ids.length <= rl.MAX_EXPR_IN_LIST) List(ids)
        else {
          val splitList = ids.splitAt(rl.MAX_EXPR_IN_LIST)
          splitList._1 :: groupIDs(splitList._2)
        }
      }

      if (l.isEmpty) Filter("")
      else Filter(groupIDs(l).map(group => {
        getFullyQualifiedName + " in (" + group.mkString(", ") + ")"
      }).mkString(" OR "))
    }
  }

  def equalsConstant(d: Double): Filter = Filter(getFullyQualifiedName + " = " + d)

  def getValueFromString(s: String): Option[Double] = {
    try {
      val d = s.toDouble
      Some(d)
    } catch {
      case _: Throwable => None
    }
  }
}
