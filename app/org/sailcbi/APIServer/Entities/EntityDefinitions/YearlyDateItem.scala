package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class YearlyDateItem extends StorableClass(YearlyDateItem) {
	override object values extends ValuesObject {
		val itemId = new IntFieldValue(self, YearlyDateItem.fields.itemId)
		val itemAlias = new StringFieldValue(self, YearlyDateItem.fields.itemAlias)
		val itemDescription = new StringFieldValue(self, YearlyDateItem.fields.itemDescription)
		val alertIfUnset = new NullableBooleanFieldValue(self, YearlyDateItem.fields.alertIfUnset)
		val createdOn = new NullableDateTimeFieldValue(self, YearlyDateItem.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, YearlyDateItem.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, YearlyDateItem.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, YearlyDateItem.fields.updatedBy)
		val valIfUndef = new NullableBooleanFieldValue(self, YearlyDateItem.fields.valIfUndef)
	}
}

object YearlyDateItem extends StorableObject[YearlyDateItem] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "YEARLY_DATE_ITEMS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		@NullableInDatabase
		val itemAlias = new StringDatabaseField(self, "ITEM_ALIAS", 50)
		@NullableInDatabase
		val itemDescription = new StringDatabaseField(self, "ITEM_DESCRIPTION", 500)
		val alertIfUnset = new NullableBooleanDatabaseField(self, "ALERT_IF_UNSET")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val valIfUndef = new NullableBooleanDatabaseField(self, "VAL_IF_UNDEF")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}