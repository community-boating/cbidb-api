package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.Fields.DateDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsString, JsValue}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFieldValue(instance: StorableClass, field: DateDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[LocalDate](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = JsString(super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
}
