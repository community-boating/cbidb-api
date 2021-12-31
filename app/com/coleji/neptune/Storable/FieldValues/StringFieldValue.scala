package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.Fields.StringDatabaseField
import com.coleji.neptune.Storable.{GetSQLLiteral, GetSQLLiteralPrepared, StorableClass}
import play.api.libs.json.{JsString, JsValue}

class StringFieldValue(instance: StorableClass, field: StringDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[String](instance, field) {
	override def getPersistenceLiteral: (String, List[String]) = ("?", List(GetSQLLiteralPrepared(super.get)))

	override def asJSValue: JsValue = JsString(super.get)
}
