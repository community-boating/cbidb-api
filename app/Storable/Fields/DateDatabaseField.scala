package Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{ProtoStorable, Filter, StorableObject}

class DateDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[LocalDate](entity, persistenceFieldName) {
  val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "date"
    case x if x == classOf[OracleBroker] => "date"
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[LocalDate] = {
    row.dateFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null Date field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def isYearConstant(year: Int)(implicit pbClass: Class[_ <: PersistenceBroker]): Filter = pbClass match {
    case x if x == classOf[MysqlBroker] => {
      val jan1 = LocalDate.of(year, 1, 1)
      val nextJan1 = LocalDate.of(year+1, 1, 1)
      Filter(getFullyQualifiedName + ">= " + jan1.format(standardPattern) + " AND " + getFullyQualifiedName + " < " + nextJan1.format(standardPattern))
    }
    case x if x == classOf[OracleBroker] => Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
  }

  def isDateConstant(date: LocalDate)(implicit pbClass: Class[_ <: PersistenceBroker]): Filter = pbClass match {
    case x if x == classOf[MysqlBroker] =>
      Filter(getFullyQualifiedName + " = '" + date.format(standardPattern) + "'")
    case x if x == classOf[OracleBroker] =>
      Filter("TRUNC(" + getFullyQualifiedName + ") = TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
  }

  def getValueFromString(s: String): Option[LocalDate] = {
    try {
      Some(LocalDate.parse(s, standardPattern))
    } catch {
      case _ => None
    }
  }
}