package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoAr extends StorableClass(FoAr) {
	override object values extends ValuesObject {
		val arId = new IntFieldValue(self, FoAr.fields.arId)
		val closeId = new IntFieldValue(self, FoAr.fields.closeId)
		val sourceId = new IntFieldValue(self, FoAr.fields.sourceId)
		val value = new DoubleFieldValue(self, FoAr.fields.value)
		val createdOn = new DateTimeFieldValue(self, FoAr.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoAr.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoAr.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoAr.fields.updatedBy)
	}
}

object FoAr extends StorableObject[FoAr] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_AR"

	object fields extends FieldsObject {
		val arId = new IntDatabaseField(self, "AR_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val sourceId = new IntDatabaseField(self, "SOURCE_ID")
		val value = new DoubleDatabaseField(self, "VALUE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.arId
}