package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableStringDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteralPrepared, StorableClass}
import play.api.libs.json.{JsNull, JsString, JsValue}

class NullableStringFieldValue(instance: StorableClass, field: NullableStringDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[String]](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = ("?", List(GetSQLLiteralPrepared(super.get)))

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v)
	}
}
