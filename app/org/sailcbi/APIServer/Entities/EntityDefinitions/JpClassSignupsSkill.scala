package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassSignupsSkill extends StorableClass(JpClassSignupsSkill) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, JpClassSignupsSkill.fields.assignId)
		val signupId = new IntFieldValue(self, JpClassSignupsSkill.fields.signupId)
		val skillId = new IntFieldValue(self, JpClassSignupsSkill.fields.skillId)
		val ratingId = new NullableIntFieldValue(self, JpClassSignupsSkill.fields.ratingId)
		val changedDatetime = new DateTimeFieldValue(self, JpClassSignupsSkill.fields.changedDatetime)
		val instructor = new NullableStringFieldValue(self, JpClassSignupsSkill.fields.instructor)
		val comments = new NullableUnknownFieldType(self, JpClassSignupsSkill.fields.comments)
		val createdOn = new DateTimeFieldValue(self, JpClassSignupsSkill.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpClassSignupsSkill.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassSignupsSkill.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpClassSignupsSkill.fields.updatedBy)
	}
}

object JpClassSignupsSkill extends StorableObject[JpClassSignupsSkill] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_SIGNUPS_SKILLS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val skillId = new IntDatabaseField(self, "SKILL_ID")
		val ratingId = new NullableIntDatabaseField(self, "RATING_ID")
		val changedDatetime = new DateTimeDatabaseField(self, "CHANGED_DATETIME")
		val instructor = new NullableStringDatabaseField(self, "INSTRUCTOR", 500)
		val comments = new NullableUnknownFieldType(self, "COMMENTS")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}