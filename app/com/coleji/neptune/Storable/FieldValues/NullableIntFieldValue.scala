package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableIntDatabaseField
import com.coleji.neptune.Storable.StorableClass
import play.api.libs.json.{JsNull, JsNumber, JsValue}

class NullableIntFieldValue(instance: StorableClass, field: NullableIntDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[Int]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.get match {
		case Some(x) => (x.toString, List.empty)
		case None => ("NULL", List.empty)
	}

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsNumber(v)
	}
}