package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoAlert extends StorableClass(FoAlert) {
	object values extends ValuesObject {
		val alert = new NullableStringFieldValue(self, FoAlert.fields.alert)
	}
}

object FoAlert extends StorableObject[FoAlert] {
	val entityName: String = "FO_ALERTS"

	object fields extends FieldsObject {
		val alert = new NullableStringDatabaseField(self, "ALERT", 294)
	}
}