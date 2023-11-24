package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.DoubleDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsNull, JsNumber, JsValue}

class DoubleFieldValue(instance: StorableClass, @transient fieldInner: DoubleDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Double](instance, fieldInner) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = JsNumber(super.get)

	override def updateFromJsValue(v: JsValue): Boolean = v match {
		case n: JsNumber => update(n.value.doubleValue)
		case JsNull => throw new Exception("JsNull provided to nonnull field " + field.getRuntimeFieldName)
		case _ => false
	}
}
