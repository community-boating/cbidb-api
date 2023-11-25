package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoParking extends StorableClass(FoParking) {
	override object values extends ValuesObject {
		val closeId = new IntFieldValue(self, FoParking.fields.closeId)
		val openCt = new NullableIntFieldValue(self, FoParking.fields.openCt)
		val closeCt = new NullableIntFieldValue(self, FoParking.fields.closeCt)
		val changeCt = new NullableIntFieldValue(self, FoParking.fields.changeCt)
		val createdOn = new DateTimeFieldValue(self, FoParking.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoParking.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoParking.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoParking.fields.updatedBy)
		val compCt = new NullableIntFieldValue(self, FoParking.fields.compCt)
		val unitPrice = new DoubleFieldValue(self, FoParking.fields.unitPrice)
	}
}

object FoParking extends StorableObject[FoParking] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_PARKING"

	object fields extends FieldsObject {
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val openCt = new NullableIntDatabaseField(self, "OPEN_CT")
		val closeCt = new NullableIntDatabaseField(self, "CLOSE_CT")
		val changeCt = new NullableIntDatabaseField(self, "CHANGE_CT")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val compCt = new NullableIntDatabaseField(self, "COMP_CT")
		val unitPrice = new DoubleDatabaseField(self, "UNIT_PRICE")
	}

	def primaryKey: IntDatabaseField = fields.closeId
}