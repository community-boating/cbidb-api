package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassSkill extends StorableClass(JpClassSkill) {
	override object values extends ValuesObject {
		val skillId = new IntFieldValue(self, JpClassSkill.fields.skillId)
		val typeId = new IntFieldValue(self, JpClassSkill.fields.typeId)
		val skillName = new StringFieldValue(self, JpClassSkill.fields.skillName)
		val assessment = new StringFieldValue(self, JpClassSkill.fields.assessment)
		val createdOn = new DateTimeFieldValue(self, JpClassSkill.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpClassSkill.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassSkill.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpClassSkill.fields.updatedBy)
		val displayOrder = new DoubleFieldValue(self, JpClassSkill.fields.displayOrder)
		val active = new NullableBooleanFieldValue(self, JpClassSkill.fields.active)
	}
}

object JpClassSkill extends StorableObject[JpClassSkill] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_SKILLS"

	object fields extends FieldsObject {
		val skillId = new IntDatabaseField(self, "SKILL_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val skillName = new StringDatabaseField(self, "SKILL_NAME", 200)
		val assessment = new StringDatabaseField(self, "ASSESSMENT", 1000)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.skillId
}