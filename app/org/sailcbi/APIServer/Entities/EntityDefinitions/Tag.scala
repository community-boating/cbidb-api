package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Tag extends StorableClass(Tag) {
	override object values extends ValuesObject {
		val tagId = new IntFieldValue(self, Tag.fields.tagId)
		val tagName = new StringFieldValue(self, Tag.fields.tagName)
		val active = new NullableBooleanFieldValue(self, Tag.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, Tag.fields.displayOrder)
		val createdOn = new DateTimeFieldValue(self, Tag.fields.createdOn)
		val createdBy = new StringFieldValue(self, Tag.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Tag.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Tag.fields.updatedBy)
	}
}

object Tag extends StorableObject[Tag] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "TAGS"

	object fields extends FieldsObject {
		val tagId = new IntDatabaseField(self, "TAG_ID")
		val tagName = new StringDatabaseField(self, "TAG_NAME", 100)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.tagId
}