package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Storable.Fields.NullableBooleanDatabaseField
import com.coleji.framework.Storable.StorableClass
import play.api.libs.json.{JsBoolean, JsNull, JsValue}

class NullableBooleanFieldValue (instance: StorableClass, field: NullableBooleanDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[Boolean]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val value = super.get match {
			case Some(true) => "'Y'"
			case Some(false) => "'N'"
			case None => "NULL"
		}
		(value, List.empty)
	}

	override def asJSValue: JsValue = super.get match {
		case Some(true) => JsBoolean(true)
		case Some(false) => JsBoolean(false)
		case None => JsNull
	}
}

