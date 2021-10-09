package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.Fields.NullableDateTimeDatabaseField
import com.coleji.neptune.Storable.StorableClass
import play.api.libs.json.{JsNull, JsString, JsValue}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NullableDateTimeFieldValue(instance: StorableClass, field: NullableDateTimeDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[LocalDateTime]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val d = super.get
		d match {
			case None => ("NULL", List.empty)
			case Some(d) => persistenceSystem match {
				case PERSISTENCE_SYSTEM_MYSQL => ("'" + d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'", List.empty)
				case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + d.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")) + "', 'MM/DD/YYYY HH:MI:SS')", List.empty)
			}
		}
	}

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
	}
}