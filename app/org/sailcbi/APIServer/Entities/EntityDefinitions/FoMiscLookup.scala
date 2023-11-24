package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoMiscLookup extends StorableClass(FoMiscLookup) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, FoMiscLookup.fields.itemId)
		val itemName = new StringFieldValue(self, FoMiscLookup.fields.itemName)
		val active = new NullableBooleanFieldValue(self, FoMiscLookup.fields.active)
		val displayOrder = new DoubleFieldValue(self, FoMiscLookup.fields.displayOrder)
		val regCodeRowId = new NullableIntFieldValue(self, FoMiscLookup.fields.regCodeRowId)
		val createdOn = new NullableDateTimeFieldValue(self, FoMiscLookup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoMiscLookup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoMiscLookup.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoMiscLookup.fields.updatedBy)
		val taxable = new NullableBooleanFieldValue(self, FoMiscLookup.fields.taxable)
	}
}

object FoMiscLookup extends StorableObject[FoMiscLookup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_MISC_LOOKUP"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		@NullableInDatabase
		val itemName = new StringDatabaseField(self, "ITEM_NAME", 200)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		val regCodeRowId = new NullableIntDatabaseField(self, "REG_CODE_ROW_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val taxable = new NullableBooleanDatabaseField(self, "TAXABLE")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}