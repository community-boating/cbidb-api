package org.sailcbi.APIServer.Storable.Fields.FieldValue

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.Fields.DateDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class DateFieldValue(instance: StorableClass, field: DateDatabaseField) extends FieldValue[LocalDate](instance, field) {
	def getPersistenceLiteral: String = PermissionsAuthority.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'"
		case PERSISTENCE_SYSTEM_ORACLE => "TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')"
	}
}
