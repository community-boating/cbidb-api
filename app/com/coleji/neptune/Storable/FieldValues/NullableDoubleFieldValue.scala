package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableDoubleDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsNull, JsNumber, JsValue}

class NullableDoubleFieldValue(instance: StorableClass, field: NullableDoubleDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[Double]](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsNumber(v)
	}

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case n: JsNumber => update(Some(n.value.doubleValue()))
		case JsNull => update(None)
		case _ => false
	}
}