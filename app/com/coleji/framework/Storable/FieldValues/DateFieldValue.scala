package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.framework.Storable.Fields.DateDatabaseField
import com.coleji.framework.Storable.StorableClass
import play.api.libs.json.{JsString, JsValue}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFieldValue(instance: StorableClass, field: DateDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[LocalDate](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => ("'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'", List.empty)
		case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')", List.empty)
	}

	override def asJSValue: JsValue = JsString(super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
}
