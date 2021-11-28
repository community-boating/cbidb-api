package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CcTransNumber extends StorableClass(CcTransNumber) {
	object values extends ValuesObject {
		val closeId = new NullableIntFieldValue(self, CcTransNumber.fields.closeId)
		val ccTransNum = new NullableDoubleFieldValue(self, CcTransNumber.fields.ccTransNum)
		val nameLast = new NullableStringFieldValue(self, CcTransNumber.fields.nameLast)
		val nameFirst = new NullableStringFieldValue(self, CcTransNumber.fields.nameFirst)
		val itemName = new NullableStringFieldValue(self, CcTransNumber.fields.itemName)
		val discount = new NullableStringFieldValue(self, CcTransNumber.fields.discount)
		val price = new NullableDoubleFieldValue(self, CcTransNumber.fields.price)
	}
}

object CcTransNumber extends StorableObject[CcTransNumber] {
	val entityName: String = "CC_TRANS_NUMBERS"

	object fields extends FieldsObject {
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val itemName = new NullableStringDatabaseField(self, "ITEM_NAME", 200)
		val discount = new NullableStringDatabaseField(self, "DISCOUNT", 100)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
	}
}