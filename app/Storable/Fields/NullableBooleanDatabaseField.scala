package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{DatabaseRow, Filter, StorableObject}

class NullableBooleanDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField[Option[Boolean]](entity, fieldName) {
  def getFieldLength: Int = 1

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "char(1)"
      case x if x == classOf[OracleBroker]  => "char(1)"
    }
  }

  def getValue(row: DatabaseRow): Option[Boolean] = {
    row.stringFields.get(fieldName) match {
      case Some(Some("Y")) => Some(true)
      case Some(Some("N")) => Some(false)
      case Some(None) => None
      case _ => throw new Exception("Nullable Boolean did not exist in DatabaseRow")
    }
  }

  def equals(b: Option[Boolean]): Filter = b match {
    case Some(x) => Filter(getFullyQualifiedName + " = '" + (if (x) "Y" else "N") + "'")
    case None => Filter(getFullyQualifiedName + " IS NULL")
  }
}