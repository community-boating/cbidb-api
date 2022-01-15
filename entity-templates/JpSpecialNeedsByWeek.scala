package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpSpecialNeedsByWeek extends StorableClass(JpSpecialNeedsByWeek) {
	object values extends ValuesObject {
		val nameFirst = new NullableStringFieldValue(self, JpSpecialNeedsByWeek.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, JpSpecialNeedsByWeek.fields.nameLast)
		val allergies = new NullableStringFieldValue(self, JpSpecialNeedsByWeek.fields.allergies)
		val medications = new NullableStringFieldValue(self, JpSpecialNeedsByWeek.fields.medications)
		val specialNeeds = new NullableStringFieldValue(self, JpSpecialNeedsByWeek.fields.specialNeeds)
		val season = new NullableDoubleFieldValue(self, JpSpecialNeedsByWeek.fields.season)
		val week = new NullableDoubleFieldValue(self, JpSpecialNeedsByWeek.fields.week)
	}
}

object JpSpecialNeedsByWeek extends StorableObject[JpSpecialNeedsByWeek] {
	val entityName: String = "JP_SPECIAL_NEEDS_BY_WEEK"

	object fields extends FieldsObject {
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val allergies = new NullableStringDatabaseField(self, "ALLERGIES", 4000)
		val medications = new NullableStringDatabaseField(self, "MEDICATIONS", 4000)
		val specialNeeds = new NullableStringDatabaseField(self, "SPECIAL_NEEDS", 4000)
		val season = new NullableDoubleDatabaseField(self, "SEASON")
		val week = new NullableDoubleDatabaseField(self, "WEEK")
	}
}