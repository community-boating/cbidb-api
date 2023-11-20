package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoParking extends StorableClass(FoParking) {
	override object values extends ValuesObject {
		val closeId = new IntFieldValue(self, FoParking.fields.closeId)
		val openCt = new NullableDoubleFieldValue(self, FoParking.fields.openCt)
		val closeCt = new NullableDoubleFieldValue(self, FoParking.fields.closeCt)
		val changeCt = new NullableDoubleFieldValue(self, FoParking.fields.changeCt)
		val createdOn = new DateTimeFieldValue(self, FoParking.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoParking.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoParking.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoParking.fields.updatedBy)
		val compCt = new NullableDoubleFieldValue(self, FoParking.fields.compCt)
		val unitPrice = new DoubleFieldValue(self, FoParking.fields.unitPrice)
	}
}

object FoParking extends StorableObject[FoParking] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_PARKING"

	object fields extends FieldsObject {
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val openCt = new NullableDoubleDatabaseField(self, "OPEN_CT")
		val closeCt = new NullableDoubleDatabaseField(self, "CLOSE_CT")
		val changeCt = new NullableDoubleDatabaseField(self, "CHANGE_CT")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val compCt = new NullableDoubleDatabaseField(self, "COMP_CT")
		val unitPrice = new DoubleDatabaseField(self, "UNIT_PRICE")
	}

	def primaryKey: IntDatabaseField = fields.closeId
}