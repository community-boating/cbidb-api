package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OptOutGroup extends StorableClass(OptOutGroup) {
	override object values extends ValuesObject {
		val optOutGrpId = new IntFieldValue(self, OptOutGroup.fields.optOutGrpId)
		val groupName = new StringFieldValue(self, OptOutGroup.fields.groupName)
		val createdOn = new DateTimeFieldValue(self, OptOutGroup.fields.createdOn)
		val createdBy = new StringFieldValue(self, OptOutGroup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, OptOutGroup.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, OptOutGroup.fields.updatedBy)
	}
}

object OptOutGroup extends StorableObject[OptOutGroup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "OPT_OUT_GROUPS"

	object fields extends FieldsObject {
		val optOutGrpId = new IntDatabaseField(self, "OPT_OUT_GRP_ID")
		val groupName = new StringDatabaseField(self, "GROUP_NAME", 100)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.optOutGrpId
}