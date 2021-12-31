package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.Fields.DateTimeDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsString, JsValue}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeFieldValue(instance: StorableClass, field: DateTimeDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[LocalDateTime](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get, truncateToDay = false), List.empty)

	override def asJSValue: JsValue = JsString(super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
}
