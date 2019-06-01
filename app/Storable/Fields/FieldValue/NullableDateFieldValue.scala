package Storable.Fields.FieldValue

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Services.PermissionsAuthority
import Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import Storable.Fields.NullableDateDatabaseField
import Storable.StorableClass

class NullableDateFieldValue(instance: StorableClass, field: NullableDateDatabaseField) extends FieldValue[Option[LocalDate]](instance, field) {
	def getPersistenceLiteral: String = {
		val d = super.get
		d match {
			case None => "NULL"
			case Some(d) => PermissionsAuthority.getPersistenceSystem match {
				case PERSISTENCE_SYSTEM_MYSQL => "'" + d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
				case PERSISTENCE_SYSTEM_ORACLE => "TO_DATE('" + d.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
			}
		}
	}
}