package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class TempGc extends StorableClass(TempGc) {
	override object values extends ValuesObject {
		val redemptionCode = new NullableStringFieldValue(self, TempGc.fields.redemptionCode)
		val certNumber = new NullableDoubleFieldValue(self, TempGc.fields.certNumber)
	}
}

object TempGc extends StorableObject[TempGc] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "TEMP_GC"

	object fields extends FieldsObject {
		val redemptionCode = new NullableStringDatabaseField(self, "REDEMPTION_CODE", 30)
		val certNumber = new NullableDoubleDatabaseField(self, "CERT_NUMBER")
	}
}