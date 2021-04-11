package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Storable.StorableClass
import play.api.libs.json.{JsNumber, JsValue}

class IntFieldValue(instance: StorableClass, field: IntDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Int](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = (super.get.toString, List.empty)

	override def asJSValue: JsValue = JsNumber(super.get)
}
