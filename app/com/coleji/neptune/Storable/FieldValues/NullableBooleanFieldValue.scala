package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableBooleanDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsBoolean, JsNull, JsValue}

class NullableBooleanFieldValue (instance: StorableClass, field: NullableBooleanDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[Boolean]](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = super.get match {
		case Some(true) => JsBoolean(true)
		case Some(false) => JsBoolean(false)
		case None => JsNull
	}
}

