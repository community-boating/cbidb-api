package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.StringDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPrepared, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

class StringFieldValue(instance: StorableClass, field: StringDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[String](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = ("?", List(GetSQLLiteralPrepared(super.get)))

	override def asJSValue: JsValue = JsString(super.get)

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case s: JsString => update(s.value)
		case JsNull => throw new Exception("JsNull provided to nonnull field " + field.getRuntimeFieldName)
		case _ => false
	}
}
