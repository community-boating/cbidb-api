package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.Fields.NullableDateDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NullableDateFieldValue(instance: StorableClass, field: NullableDateDatabaseField)(implicit PA: PermissionsAuthority) extends FieldValue[Option[LocalDate]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val d = super.get
		d match {
			case None => ("NULL", List.empty)
			case Some(d) => PA.persistenceSystem match {
				case PERSISTENCE_SYSTEM_MYSQL => ("'" + d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'", List.empty)
				case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + d.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')", List.empty)
			}
		}
	}
}