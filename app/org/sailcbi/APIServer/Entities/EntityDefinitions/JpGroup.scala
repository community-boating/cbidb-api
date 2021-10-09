package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpGroup extends StorableClass(JpGroup) {
	object values extends ValuesObject {
		val groupId = new IntFieldValue(self, JpGroup.fields.groupId)
		val groupName = new StringFieldValue(self, JpGroup.fields.groupName)
	}
}

object JpGroup extends StorableObject[JpGroup] {
	val entityName: String = "JP_GROUPS"

	object fields extends FieldsObject {
		val groupId = new IntDatabaseField(self, "GROUP_ID")
		@NullableInDatabase
		val groupName = new StringDatabaseField(self, "GROUP_NAME", 200)
	}

	def primaryKey: IntDatabaseField = fields.groupId
}