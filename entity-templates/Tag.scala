package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Tag extends StorableClass(Tag) {
	object values extends ValuesObject {
		val tagId = new IntFieldValue(self, Tag.fields.tagId)
		val tagName = new NullableStringFieldValue(self, Tag.fields.tagName)
		val active = new NullableBooleanFIeldValue(self, Tag.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, Tag.fields.displayOrder)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Tag.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Tag.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Tag.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Tag.fields.updatedBy)
	}
}

object Tag extends StorableObject[Tag] {
	val entityName: String = "TAGS"

	object fields extends FieldsObject {
		val tagId = new IntDatabaseField(self, "TAG_ID")
		val tagName = new NullableStringDatabaseField(self, "TAG_NAME", 100)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.tagId
}