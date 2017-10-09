package Storable.Fields

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class DateTimeDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[LocalDateTime](entity, persistenceFieldName) {
  val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "datetime"
    case x if x == classOf[OracleBroker] => "date"
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[LocalDateTime] = {
    row.dateTimeFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null DateTime field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def isYearConstant(year: Int)(implicit pbClass: Class[_ <: PersistenceBroker]): Filter = pbClass match {
    case x if x == classOf[MysqlBroker] => {
      val jan1 = LocalDate.of(year, 1, 1)
      val nextJan1 = LocalDate.of(year+1, 1, 1)
      val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      Filter(getFullyQualifiedName + ">= " + jan1.format(pattern) + " AND " + getFullyQualifiedName + " < " + nextJan1.format(pattern))
    }
    case x if x == classOf[OracleBroker] => Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
  }

  def isDateConstant(date: LocalDate)(implicit pbClass: Class[_ <: PersistenceBroker]): Filter = pbClass match {
    case x if x == classOf[MysqlBroker] =>
      Filter(
        getFullyQualifiedName + " >= '" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'" + " AND " +
        getFullyQualifiedName + " < '" + date.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
      )
    case x if x == classOf[OracleBroker] =>
      Filter("TRUNC(" + getFullyQualifiedName + ") = TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
  }

  def getValueFromString(s: String): Option[LocalDateTime] = {
    try {
      Some(LocalDateTime.parse(s, standardPattern))
    } catch {
      case _ => None
    }
  }
}