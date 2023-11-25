package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpNoSwim extends StorableClass(JpNoSwim) {
	override object values extends ValuesObject {
		val swimId = new IntFieldValue(self, JpNoSwim.fields.swimId)
		val cardNum = new StringFieldValue(self, JpNoSwim.fields.cardNum)
		val createdOn = new DateTimeFieldValue(self, JpNoSwim.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpNoSwim.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpNoSwim.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpNoSwim.fields.updatedBy)
	}
}

object JpNoSwim extends StorableObject[JpNoSwim] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_NO_SWIM"

	object fields extends FieldsObject {
		val swimId = new IntDatabaseField(self, "SWIM_ID")
		@NullableInDatabase
		val cardNum = new StringDatabaseField(self, "CARD_NUM", 20)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.swimId
}