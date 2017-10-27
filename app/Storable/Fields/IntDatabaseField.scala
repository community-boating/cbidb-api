package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker, RelationalBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class IntDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[Int](entity, persistenceFieldName) {
  def getFieldType(implicit pb: PersistenceBroker): String = pb match {
    case _: MysqlBroker => "integer"
    case _: OracleBroker => "number"
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

  def inList(l: List[Int])(implicit pb: PersistenceBroker): Filter = pb match {
    case rl: RelationalBroker => {
      def groupIDs(ids: List[Int]): List[List[Int]] = {
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

  def equalsConstant(i: Int): Filter = Filter(getFullyQualifiedName + " = " + i)

  def getValueFromString(s: String): Option[Int] = {
    try {
      val d = s.toInt
      Some(d)
    } catch {
      case _: Throwable => None
    }
  }
}
