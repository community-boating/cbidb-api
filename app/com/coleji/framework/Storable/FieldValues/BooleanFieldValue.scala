package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Storable.Fields.BooleanDatabaseField
import com.coleji.framework.Storable.StorableClass
import play.api.libs.json.{JsBoolean, JsValue}

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Boolean](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val value = if (super.get) "'Y'" else "'N'"
		(value, List.empty)
	}

	override def asJSValue: JsValue = JsBoolean(super.get)
}
