package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.NullableStringDatabaseField
import com.coleji.neptune.Storable.StorableClass
import play.api.libs.json.{JsNull, JsString, JsValue}

class NullableStringFieldValue(instance: StorableClass, field: NullableStringDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[String]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.get match {
		case Some("") => ("NULL", List.empty)
		case Some(x) => ("?", List(x))
		case None => ("NULL", List.empty)
	}

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v)
	}
}
