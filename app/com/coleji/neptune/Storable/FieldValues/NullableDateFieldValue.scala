package com.coleji.neptune.Storable.FieldValues

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.Fields.NullableDateDatabaseField
import com.coleji.neptune.Storable.StorableClass
import play.api.libs.json.{JsNull, JsString, JsValue}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NullableDateFieldValue(instance: StorableClass, field: NullableDateDatabaseField)(implicit persistenceSystem: PersistenceSystem) extends FieldValue[Option[LocalDate]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val d = super.get
		d match {
			case None => ("NULL", List.empty)
			case Some(d) => persistenceSystem match {
				case PERSISTENCE_SYSTEM_MYSQL => ("'" + d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'", List.empty)
				case PERSISTENCE_SYSTEM_ORACLE => ("TO_DATE('" + d.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "', 'MM/DD/YYYY')", List.empty)
			}
		}
	}

	override def asJSValue: JsValue = super.get match {
		case None => JsNull
		case Some(v) => JsString(v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
	}
}