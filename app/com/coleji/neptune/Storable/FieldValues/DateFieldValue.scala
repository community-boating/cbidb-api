package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.DateDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateFieldValue(instance: StorableClass, @transient fieldInner: DateDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[LocalDate](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = JsString(super.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case s: JsString => update(LocalDate.parse(s.value))
		case JsNull => throw new Exception("JsNull provided to nonnull field " + field.getRuntimeFieldName)
		case _ => false
	}
}
