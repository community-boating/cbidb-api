package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoCheckWithX extends StorableClass(FoCheckWithX) {
	object values extends ValuesObject {
		val closeId = new NullableIntFieldValue(self, FoCheckWithX.fields.closeId)
		val code = new NullableDoubleFieldValue(self, FoCheckWithX.fields.code)
		val itemDesc = new NullableStringFieldValue(self, FoCheckWithX.fields.itemDesc)
		val ct = new NullableDoubleFieldValue(self, FoCheckWithX.fields.ct)
		val total = new NullableDoubleFieldValue(self, FoCheckWithX.fields.total)
	}
}

object FoCheckWithX extends StorableObject[FoCheckWithX] {
	val entityName: String = "FO_CHECK_WITH_X"

	object fields extends FieldsObject {
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val code = new NullableDoubleDatabaseField(self, "CODE")
		val itemDesc = new NullableStringDatabaseField(self, "ITEM_DESC", 200)
		val ct = new NullableDoubleDatabaseField(self, "CT")
		val total = new NullableDoubleDatabaseField(self, "TOTAL")
	}
}