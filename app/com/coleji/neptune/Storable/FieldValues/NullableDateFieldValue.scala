package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableDateDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NullableDateFieldValue(instance: StorableClass, field: NullableDateDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[LocalDate]](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
	}

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case s: JsString => update(Some(LocalDate.parse(s.value)))
		case JsNull => update(None)
		case _ => false
	}
}