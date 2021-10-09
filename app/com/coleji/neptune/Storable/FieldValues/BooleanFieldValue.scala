package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.BooleanDatabaseField
import com.coleji.neptune.Storable.StorableClass
import play.api.libs.json.{JsBoolean, JsValue}

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Boolean](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val value = if (super.get) "'Y'" else "'N'"
		(value, List.empty)
	}

	override def asJSValue: JsValue = JsBoolean(super.get)
}
