package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpClassSkillsRating extends StorableClass(JpClassSkillsRating) {
	override object values extends ValuesObject {
		val ratingId = new IntFieldValue(self, JpClassSkillsRating.fields.ratingId)
		val skillId = new IntFieldValue(self, JpClassSkillsRating.fields.skillId)
		val ratingName = new StringFieldValue(self, JpClassSkillsRating.fields.ratingName)
		val ratingSeq = new DoubleFieldValue(self, JpClassSkillsRating.fields.ratingSeq)
		val createdOn = new DateTimeFieldValue(self, JpClassSkillsRating.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpClassSkillsRating.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassSkillsRating.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpClassSkillsRating.fields.updatedBy)
	}
}

object JpClassSkillsRating extends StorableObject[JpClassSkillsRating] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_SKILLS_RATINGS"

	object fields extends FieldsObject {
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val skillId = new IntDatabaseField(self, "SKILL_ID")
		val ratingName = new StringDatabaseField(self, "RATING_NAME", 50)
		val ratingSeq = new DoubleDatabaseField(self, "RATING_SEQ")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.ratingId
}