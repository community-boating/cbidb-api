package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EiiOverride extends StorableClass(EiiOverride) {
	override object values extends ValuesObject {
		val personId = new IntFieldValue(self, EiiOverride.fields.personId)
		val season = new DoubleFieldValue(self, EiiOverride.fields.season)
		val price = new DoubleFieldValue(self, EiiOverride.fields.price)
		val createdOn = new DateTimeFieldValue(self, EiiOverride.fields.createdOn)
		val createdBy = new StringFieldValue(self, EiiOverride.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, EiiOverride.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, EiiOverride.fields.updatedBy)
		val overrideId = new IntFieldValue(self, EiiOverride.fields.overrideId)
	}
}

object EiiOverride extends StorableObject[EiiOverride] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EII_OVERRIDES"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val price = new DoubleDatabaseField(self, "PRICE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val overrideId = new IntDatabaseField(self, "OVERRIDE_ID")
	}

	def primaryKey: IntDatabaseField = fields.overrideId
}