package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassSkill extends StorableClass(JpClassSkill) {
	object values extends ValuesObject {
		val skillId = new IntFieldValue(self, JpClassSkill.fields.skillId)
		val typeId = new NullableIntFieldValue(self, JpClassSkill.fields.typeId)
		val skillName = new StringFieldValue(self, JpClassSkill.fields.skillName)
		val assessment = new NullableStringFieldValue(self, JpClassSkill.fields.assessment)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassSkill.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassSkill.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassSkill.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassSkill.fields.updatedBy)
		val displayOrder = new NullableDoubleFieldValue(self, JpClassSkill.fields.displayOrder)
		val active = new NullableBooleanFIeldValue(self, JpClassSkill.fields.active)
	}
}

object JpClassSkill extends StorableObject[JpClassSkill] {
	val entityName: String = "JP_CLASS_SKILLS"

	object fields extends FieldsObject {
		val skillId = new IntDatabaseField(self, "SKILL_ID")
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
		val skillName = new StringDatabaseField(self, "SKILL_NAME", 200)
		val assessment = new NullableStringDatabaseField(self, "ASSESSMENT", 1000)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.skillId
}