package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class ApClassTypeOverride extends StorableClass(ApClassTypeOverride) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, ApClassTypeOverride.fields.rowId)
		val personId = new IntFieldValue(self, ApClassTypeOverride.fields.personId)
		val typeId = new IntFieldValue(self, ApClassTypeOverride.fields.typeId)
		val season = new DoubleFieldValue(self, ApClassTypeOverride.fields.season)
		val createdOn = new NullableDateTimeFieldValue(self, ApClassTypeOverride.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassTypeOverride.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, ApClassTypeOverride.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassTypeOverride.fields.updatedBy)
		val isForOverkill = new BooleanFieldValue(self, ApClassTypeOverride.fields.isForOverkill)
	}
}

object ApClassTypeOverride extends StorableObject[ApClassTypeOverride] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_TYPE_OVERRIDES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val isForOverkill = new BooleanDatabaseField(self, "IS_FOR_OVERKILL", false)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}