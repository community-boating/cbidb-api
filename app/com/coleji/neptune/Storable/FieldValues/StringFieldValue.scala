package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.StringDatabaseField
import com.coleji.neptune.Storable.StorableClass
import play.api.libs.json.{JsString, JsValue}

class StringFieldValue(instance: StorableClass, field: StringDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[String](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.peek match {
		case Some("") => throw new Exception("Empty string not allowed in non-null string fields")
		case Some(x) => ("?", List(x))
		case None => ("NULL", List.empty)
	}

	override def asJSValue: JsValue = JsString(super.get)
}
