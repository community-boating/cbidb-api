package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class UapGroup extends StorableClass(UapGroup) {
	override object values extends ValuesObject {
		val uapGroupId = new IntFieldValue(self, UapGroup.fields.uapGroupId)
		val groupName = new NullableStringFieldValue(self, UapGroup.fields.groupName)
		val createdOn = new DateTimeFieldValue(self, UapGroup.fields.createdOn)
		val createdBy = new StringFieldValue(self, UapGroup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, UapGroup.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, UapGroup.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, UapGroup.fields.active)
	}
}

object UapGroup extends StorableObject[UapGroup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "UAP_GROUPS"

	object fields extends FieldsObject {
		val uapGroupId = new IntDatabaseField(self, "UAP_GROUP_ID")
		val groupName = new NullableStringDatabaseField(self, "GROUP_NAME", 50)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.uapGroupId
}