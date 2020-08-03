package org.sailcbi.APIServer.Storable.FieldValues

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.Fields.DateTimeDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class DateTimeFieldValue(instance: StorableClass, field: DateTimeDatabaseField)(implicit PA: PermissionsAuthority) extends FieldValue[LocalDateTime](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => ("'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'", List.empty)
		case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH:MI:SS')", List.empty)
	}
}
