package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class HighSchool extends StorableClass(HighSchool) {
	override object values extends ValuesObject {
		val schoolId = new IntFieldValue(self, HighSchool.fields.schoolId)
		val schoolName = new StringFieldValue(self, HighSchool.fields.schoolName)
		val createdOn = new DateTimeFieldValue(self, HighSchool.fields.createdOn)
		val createdBy = new StringFieldValue(self, HighSchool.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, HighSchool.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, HighSchool.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, HighSchool.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, HighSchool.fields.displayOrder)
	}
}

object HighSchool extends StorableObject[HighSchool] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "HIGH_SCHOOLS"

	object fields extends FieldsObject {
		val schoolId = new IntDatabaseField(self, "SCHOOL_ID")
		@NullableInDatabase
		val schoolName = new StringDatabaseField(self, "SCHOOL_NAME", 200)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.schoolId
}