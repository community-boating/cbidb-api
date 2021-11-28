package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CloseDescription extends StorableClass(CloseDescription) {
	object values extends ValuesObject {
		val closeDescription = new NullableStringFieldValue(self, CloseDescription.fields.closeDescription)
		val closeId = new IntFieldValue(self, CloseDescription.fields.closeId)
	}
}

object CloseDescription extends StorableObject[CloseDescription] {
	val entityName: String = "CLOSE_DESCRIPTIONS"

	object fields extends FieldsObject {
		val closeDescription = new NullableStringDatabaseField(self, "CLOSE_DESCRIPTION", 129)
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
	}
}