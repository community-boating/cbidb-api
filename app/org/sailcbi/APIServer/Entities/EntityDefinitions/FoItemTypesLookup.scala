package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoItemTypesLookup extends StorableClass(FoItemTypesLookup) {
	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, FoItemTypesLookup.fields.typeId)
		val itemId = new IntFieldValue(self, FoItemTypesLookup.fields.itemId)
		val description = new StringFieldValue(self, FoItemTypesLookup.fields.description)
		val createdOn = new NullableDateTimeFieldValue(self, FoItemTypesLookup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoItemTypesLookup.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, FoItemTypesLookup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoItemTypesLookup.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, FoItemTypesLookup.fields.active)
		val displayOrder = new DoubleFieldValue(self, FoItemTypesLookup.fields.displayOrder)
	}
}

object FoItemTypesLookup extends StorableObject[FoItemTypesLookup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_ITEM_TYPES_LOOKUP"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		@NullableInDatabase
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		@NullableInDatabase
		val description = new StringDatabaseField(self, "DESCRIPTION", 100)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}