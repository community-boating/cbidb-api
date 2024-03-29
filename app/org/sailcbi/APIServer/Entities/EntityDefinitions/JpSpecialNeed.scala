package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpSpecialNeed extends StorableClass(JpSpecialNeed) {
	override object values extends ValuesObject {
		val cardId = new IntFieldValue(self, JpSpecialNeed.fields.cardId)
		val cardNum = new NullableStringFieldValue(self, JpSpecialNeed.fields.cardNum)
		val personId = new NullableIntFieldValue(self, JpSpecialNeed.fields.personId)
		val createdOn = new DateTimeFieldValue(self, JpSpecialNeed.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpSpecialNeed.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpSpecialNeed.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpSpecialNeed.fields.updatedBy)
	}
}

object JpSpecialNeed extends StorableObject[JpSpecialNeed] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_SPECIAL_NEEDS"

	object fields extends FieldsObject {
		val cardId = new IntDatabaseField(self, "CARD_ID")
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.cardId
}