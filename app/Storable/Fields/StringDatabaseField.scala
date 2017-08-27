package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, StorableObject}
import oracle.net.aso.q

class StringDatabaseField(entity: StorableObject[_], fieldName: String, fieldLength: Int) extends DatabaseField(entity, fieldName) {
  def getFieldLength: Int = fieldLength

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "varchar(" + getFieldLength + ")"
      case x if x == classOf[OracleBroker]  => "varchar2(" + getFieldLength + ")"
    }
  }





  def equalsConstant(c: String): Filter  =
    Filter(getFullyQualifiedName + " = '" + c + "'")
}