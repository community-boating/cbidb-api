package Storable.Fields.FieldValue

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import Services.PermissionsAuthority
import Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import Storable.Fields.DateTimeDatabaseField
import Storable.StorableClass

class DateTimeFieldValue(instance: StorableClass, field: DateTimeDatabaseField) extends FieldValue[LocalDateTime](instance, field) {
  def getPersistenceLiteral: String = PermissionsAuthority.getPersistenceSystem match {
    case PERSISTENCE_SYSTEM_MYSQL => "'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'"
    case PERSISTENCE_SYSTEM_ORACLE => "TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH:MI:SS')"
  }
}
