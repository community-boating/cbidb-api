package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableDateTimeDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NullableDateTimeFieldValue(instance: StorableClass, field: NullableDateTimeDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[LocalDateTime]](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
	}
}