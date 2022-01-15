package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UapGroup extends StorableClass(UapGroup) {
	object values extends ValuesObject {
		val uapGroupId = new IntFieldValue(self, UapGroup.fields.uapGroupId)
		val groupName = new NullableStringFieldValue(self, UapGroup.fields.groupName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, UapGroup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, UapGroup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, UapGroup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, UapGroup.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, UapGroup.fields.active)
	}
}

object UapGroup extends StorableObject[UapGroup] {
	val entityName: String = "UAP_GROUPS"

	object fields extends FieldsObject {
		val uapGroupId = new IntDatabaseField(self, "UAP_GROUP_ID")
		val groupName = new NullableStringDatabaseField(self, "GROUP_NAME", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.uapGroupId
}