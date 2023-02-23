package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableBooleanDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsBoolean, JsNull, JsValue}

class NullableBooleanFieldValue (instance: StorableClass, @transient fieldInner: NullableBooleanDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[Boolean]](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = super.get match {
		case Some(true) => JsBoolean(true)
		case Some(false) => JsBoolean(false)
		case None => JsNull
	}

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case b: JsBoolean => update(Some(b.value))
		case JsNull => update(None)
		case _ => false
	}
}

