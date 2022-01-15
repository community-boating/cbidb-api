package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassSkillsRating extends StorableClass(JpClassSkillsRating) {
	object values extends ValuesObject {
		val ratingId = new IntFieldValue(self, JpClassSkillsRating.fields.ratingId)
		val skillId = new IntFieldValue(self, JpClassSkillsRating.fields.skillId)
		val ratingName = new StringFieldValue(self, JpClassSkillsRating.fields.ratingName)
		val ratingSeq = new DoubleFieldValue(self, JpClassSkillsRating.fields.ratingSeq)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassSkillsRating.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassSkillsRating.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassSkillsRating.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassSkillsRating.fields.updatedBy)
	}
}

object JpClassSkillsRating extends StorableObject[JpClassSkillsRating] {
	val entityName: String = "JP_CLASS_SKILLS_RATINGS"

	object fields extends FieldsObject {
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val skillId = new IntDatabaseField(self, "SKILL_ID")
		val ratingName = new StringDatabaseField(self, "RATING_NAME", 50)
		val ratingSeq = new DoubleDatabaseField(self, "RATING_SEQ")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.ratingId
}