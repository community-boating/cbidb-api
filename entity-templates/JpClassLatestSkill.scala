package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassLatestSkill extends StorableClass(JpClassLatestSkill) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, JpClassLatestSkill.fields.personId)
		val typeId = new IntFieldValue(self, JpClassLatestSkill.fields.typeId)
		val skillId = new NullableIntFieldValue(self, JpClassLatestSkill.fields.skillId)
		val skillName = new StringFieldValue(self, JpClassLatestSkill.fields.skillName)
		val ratingName = new StringFieldValue(self, JpClassLatestSkill.fields.ratingName)
		val displayOrder = new NullableDoubleFieldValue(self, JpClassLatestSkill.fields.displayOrder)
		val week = new NullableStringFieldValue(self, JpClassLatestSkill.fields.week)
	}
}

object JpClassLatestSkill extends StorableObject[JpClassLatestSkill] {
	val entityName: String = "JP_CLASS_LATEST_SKILLS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val skillId = new NullableIntDatabaseField(self, "SKILL_ID")
		val skillName = new StringDatabaseField(self, "SKILL_NAME", 200)
		val ratingName = new StringDatabaseField(self, "RATING_NAME", 50)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val week = new NullableStringDatabaseField(self, "WEEK", 45)
	}
}