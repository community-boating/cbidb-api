package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoAppliedGc extends StorableClass(FoAppliedGc) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, FoAppliedGc.fields.rowId)
		val closeId = new IntFieldValue(self, FoAppliedGc.fields.closeId)
		val certId = new IntFieldValue(self, FoAppliedGc.fields.certId)
		val value = new DoubleFieldValue(self, FoAppliedGc.fields.value)
		val createdOn = new DateTimeFieldValue(self, FoAppliedGc.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoAppliedGc.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoAppliedGc.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoAppliedGc.fields.updatedBy)
		val pmAssignId = new NullableIntFieldValue(self, FoAppliedGc.fields.pmAssignId)
		val orderId = new NullableIntFieldValue(self, FoAppliedGc.fields.orderId)
		val voidCloseId = new NullableIntFieldValue(self, FoAppliedGc.fields.voidCloseId)
	}
}

object FoAppliedGc extends StorableObject[FoAppliedGc] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_APPLIED_GC"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		@NullableInDatabase
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		@NullableInDatabase
		val certId = new IntDatabaseField(self, "CERT_ID")
		@NullableInDatabase
		val value = new DoubleDatabaseField(self, "VALUE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val pmAssignId = new NullableIntDatabaseField(self, "PM_ASSIGN_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}