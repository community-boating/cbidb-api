package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpClassTypeOverride extends StorableClass(JpClassTypeOverride) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, JpClassTypeOverride.fields.rowId)
		val personId = new IntFieldValue(self, JpClassTypeOverride.fields.personId)
		val typeId = new IntFieldValue(self, JpClassTypeOverride.fields.typeId)
		val season = new NullableIntFieldValue(self, JpClassTypeOverride.fields.season)
		val isForOverkill = new BooleanFieldValue(self, JpClassTypeOverride.fields.isForOverkill)
		val createdOn = new DateTimeFieldValue(self, JpClassTypeOverride.fields.createdOn)
		val createdBy = new StringFieldValue(self, JpClassTypeOverride.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassTypeOverride.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, JpClassTypeOverride.fields.updatedBy)
	}
}

object JpClassTypeOverride extends StorableObject[JpClassTypeOverride] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_TYPE_OVERRIDES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val season = new NullableIntDatabaseField(self, "SEASON")
		val isForOverkill = new BooleanDatabaseField(self, "IS_FOR_OVERKILL", false)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}