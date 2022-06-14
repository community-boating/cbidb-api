package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.DateTimeDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

import java.time.{LocalDate, LocalDateTime, ZonedDateTime}
import java.time.format.DateTimeFormatter

class DateTimeFieldValue(instance: StorableClass, field: DateTimeDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[LocalDateTime](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get, truncateToDay = false), List.empty)

	override def asJSValue: JsValue = JsString(super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case s: JsString => update(LocalDateTime.parse(s.value))
		case JsNull => throw new Exception("JsNull provided to nonnull field")
		case _ => false
	}
}
