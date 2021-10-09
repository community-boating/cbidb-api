package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.Fields.DateTimeDatabaseField
import com.coleji.neptune.Storable.StorableClass
import play.api.libs.json.{JsString, JsValue}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFieldValue(instance: StorableClass, field: DateTimeDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[LocalDateTime](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => ("'" + super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'", List.empty)
		case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + super.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH:MI:SS')", List.empty)
	}

	override def asJSValue: JsValue = JsString(super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
}
