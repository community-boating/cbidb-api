package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{DatabaseRow, StorableObject}

class DoubleDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField[Double](entity, fieldName) {
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "decimal"
    case x if x == classOf[OracleBroker] => "number"
  }

  def getValue(row: DatabaseRow): Double = getOptionValue(row) match {
    case Some(x) => x
    case None => throw new Exception("Non-null field was null")
  }

  def getOptionValue(row: DatabaseRow): Option[Double] = {
    row.doubleFields.get(fieldName) match {
      case Some(Some(x)) => Some(x)
      case _ => None
    }
  }
}