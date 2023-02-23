package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableStringDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPrepared, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

class NullableStringFieldValue(instance: StorableClass, @transient fieldInner: NullableStringDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[String]](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = ("?", List(GetSQLLiteralPrepared(super.get)))

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v)
	}

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case s: JsString => update(Some(s.value))
		case JsNull => update(None)
		case _ => false
	}
}
