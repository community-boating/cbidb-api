package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpSpecialNeedsCopy extends StorableClass(JpSpecialNeedsCopy) {
	object values extends ValuesObject {
		val cardId = new NullableIntFieldValue(self, JpSpecialNeedsCopy.fields.cardId)
		val cardNum = new NullableStringFieldValue(self, JpSpecialNeedsCopy.fields.cardNum)
		val personId = new NullableIntFieldValue(self, JpSpecialNeedsCopy.fields.personId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpSpecialNeedsCopy.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpSpecialNeedsCopy.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpSpecialNeedsCopy.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpSpecialNeedsCopy.fields.updatedBy)
	}
}

object JpSpecialNeedsCopy extends StorableObject[JpSpecialNeedsCopy] {
	val entityName: String = "JP_SPECIAL_NEEDS_COPY"

	object fields extends FieldsObject {
		val cardId = new NullableIntDatabaseField(self, "CARD_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}
}