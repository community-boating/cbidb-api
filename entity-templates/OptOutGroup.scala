package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OptOutGroup extends StorableClass(OptOutGroup) {
	object values extends ValuesObject {
		val optOutGrpId = new IntFieldValue(self, OptOutGroup.fields.optOutGrpId)
		val groupName = new NullableStringFieldValue(self, OptOutGroup.fields.groupName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, OptOutGroup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, OptOutGroup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, OptOutGroup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, OptOutGroup.fields.updatedBy)
	}
}

object OptOutGroup extends StorableObject[OptOutGroup] {
	val entityName: String = "OPT_OUT_GROUPS"

	object fields extends FieldsObject {
		val optOutGrpId = new IntDatabaseField(self, "OPT_OUT_GRP_ID")
		val groupName = new NullableStringDatabaseField(self, "GROUP_NAME", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.optOutGrpId
}