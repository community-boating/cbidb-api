package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.Fields.DateDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFieldValue(instance: StorableClass, field: DateDatabaseField)(implicit PA: PermissionsAuthority) extends FieldValue[LocalDate](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => ("'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'", List.empty)
		case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')", List.empty)
	}
}
