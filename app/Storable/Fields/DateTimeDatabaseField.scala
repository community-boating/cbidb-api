package Storable.Fields

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import Services.{MysqlBroker, OracleBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class DateTimeDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[LocalDateTime](entity, persistenceFieldName) {
  val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def getFieldType: String = PermissionsAuthority.getPersistenceSystem match {
    case PERSISTENCE_SYSTEM_MYSQL => "datetime"
    case PERSISTENCE_SYSTEM_ORACLE => "date"
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[LocalDateTime] = {
    row.dateTimeFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null DateTime field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def isYearConstant(year: Int): Filter = PermissionsAuthority.getPersistenceSystem match {
    case PERSISTENCE_SYSTEM_MYSQL => {
      val jan1 = LocalDate.of(year, 1, 1)
      val nextJan1 = LocalDate.of(year+1, 1, 1)
      val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      Filter(getFullyQualifiedName + ">= " + jan1.format(pattern) + " AND " + getFullyQualifiedName + " < " + nextJan1.format(pattern))
    }
    case PERSISTENCE_SYSTEM_ORACLE => Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
  }

  def isDateConstant(date: LocalDate): Filter = PermissionsAuthority.getPersistenceSystem match {
    case PERSISTENCE_SYSTEM_MYSQL =>
      Filter(
        getFullyQualifiedName + " >= '" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'" + " AND " +
        getFullyQualifiedName + " < '" + date.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
      )
    case PERSISTENCE_SYSTEM_ORACLE =>
      Filter("TRUNC(" + getFullyQualifiedName + ") = TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
  }

  def getValueFromString(s: String): Option[LocalDateTime] = {
    try {
      Some(LocalDateTime.parse(s, standardPattern))
    } catch {
      case _: Throwable => None
    }
  }
}