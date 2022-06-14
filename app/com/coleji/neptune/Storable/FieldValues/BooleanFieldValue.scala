package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.BooleanDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsBoolean, JsValue, JsNull}

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Boolean](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = JsBoolean(super.get)

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case b: JsBoolean => update(b.value)
		case JsNull => throw new Exception("JsNull provided to nonnull field")
		case _ => false
	}
}
