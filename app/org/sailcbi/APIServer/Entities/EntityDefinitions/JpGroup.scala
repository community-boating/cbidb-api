package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpGroup extends StorableClass(JpGroup) {
	override object values extends ValuesObject {
		val groupId = new IntFieldValue(self, JpGroup.fields.groupId)
		val groupName = new StringFieldValue(self, JpGroup.fields.groupName)
		val createdOn = new DateTimeFieldValue(self, JpGroup.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpGroup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpGroup.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpGroup.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, JpGroup.fields.active)
	}
}

object JpGroup extends StorableObject[JpGroup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_GROUPS"

	object fields extends FieldsObject {
		val groupId = new IntDatabaseField(self, "GROUP_ID")
		@NullableInDatabase
		val groupName = new StringDatabaseField(self, "GROUP_NAME", 200)
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

	def primaryKey: IntDatabaseField = fields.groupId
}