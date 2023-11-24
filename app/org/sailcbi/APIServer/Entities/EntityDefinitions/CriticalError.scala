package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class CriticalError extends StorableClass(CriticalError) {
	override object values extends ValuesObject {
		val errorText = new NullableStringFieldValue(self, CriticalError.fields.errorText)
		val isMinor = new NullableBooleanFieldValue(self, CriticalError.fields.isMinor)
	}
}

object CriticalError extends StorableObject[CriticalError] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CRITICAL_ERRORS"

	object fields extends FieldsObject {
		val errorText = new NullableStringDatabaseField(self, "ERROR_TEXT", 4000)
		val isMinor = new NullableBooleanDatabaseField(self, "IS_MINOR")
	}
}