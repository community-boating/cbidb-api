package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.IntDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, StorableClass}
import play.api.libs.json.{JsNumber, JsValue}

class IntFieldValue(instance: StorableClass, field: IntDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Int](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = (GetSQLLiteral(super.get), List.empty)

	override def asJSValue: JsValue = JsNumber(super.get)
}
