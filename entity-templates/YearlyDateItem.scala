package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class YearlyDateItem extends StorableClass(YearlyDateItem) {
	object values extends ValuesObject {
		val itemId = new IntFieldValue(self, YearlyDateItem.fields.itemId)
		val itemAlias = new NullableStringFieldValue(self, YearlyDateItem.fields.itemAlias)
		val itemDescription = new NullableStringFieldValue(self, YearlyDateItem.fields.itemDescription)
		val alertIfUnset = new NullableBooleanFIeldValue(self, YearlyDateItem.fields.alertIfUnset)
		val createdOn = new NullableLocalDateTimeFieldValue(self, YearlyDateItem.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, YearlyDateItem.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, YearlyDateItem.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, YearlyDateItem.fields.updatedBy)
		val valIfUndef = new NullableBooleanFIeldValue(self, YearlyDateItem.fields.valIfUndef)
	}
}

object YearlyDateItem extends StorableObject[YearlyDateItem] {
	val entityName: String = "YEARLY_DATE_ITEMS"

	object fields extends FieldsObject {
		val itemId = new IntDatabaseField(self, "ITEM_ID")
		val itemAlias = new NullableStringDatabaseField(self, "ITEM_ALIAS", 50)
		val itemDescription = new NullableStringDatabaseField(self, "ITEM_DESCRIPTION", 500)
		val alertIfUnset = new NullableBooleanDatabaseField(self, "ALERT_IF_UNSET")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val valIfUndef = new NullableBooleanDatabaseField(self, "VAL_IF_UNDEF")
	}

	def primaryKey: IntDatabaseField = fields.itemId
}