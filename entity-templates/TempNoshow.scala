package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class TempNoshow extends StorableClass(TempNoshow) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, TempNoshow.fields.typeId)
		val week = new NullableDoubleFieldValue(self, TempNoshow.fields.week)
		val noshow = new NullableDoubleFieldValue(self, TempNoshow.fields.noshow)
	}
}

object TempNoshow extends StorableObject[TempNoshow] {
	val entityName: String = "TEMP_NOSHOW"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val week = new NullableDoubleDatabaseField(self, "WEEK")
		val noshow = new NullableDoubleDatabaseField(self, "NOSHOW")
	}
}