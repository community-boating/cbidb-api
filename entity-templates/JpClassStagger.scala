package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassStagger extends StorableClass(JpClassStagger) {
	object values extends ValuesObject {
		val staggerId = new IntFieldValue(self, JpClassStagger.fields.staggerId)
		val instanceId = new IntFieldValue(self, JpClassStagger.fields.instanceId)
		val staggerDate = new LocalDateTimeFieldValue(self, JpClassStagger.fields.staggerDate)
		val occupancy = new DoubleFieldValue(self, JpClassStagger.fields.occupancy)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassStagger.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassStagger.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassStagger.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassStagger.fields.updatedBy)
		val reserveOnly = new NullableBooleanFIeldValue(self, JpClassStagger.fields.reserveOnly)
		val reserveRelease = new NullableLocalDateTimeFieldValue(self, JpClassStagger.fields.reserveRelease)
	}
}

object JpClassStagger extends StorableObject[JpClassStagger] {
	val entityName: String = "JP_CLASS_STAGGERS"

	object fields extends FieldsObject {
		val staggerId = new IntDatabaseField(self, "STAGGER_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val staggerDate = new LocalDateTimeDatabaseField(self, "STAGGER_DATE")
		val occupancy = new DoubleDatabaseField(self, "OCCUPANCY")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val reserveOnly = new NullableBooleanDatabaseField(self, "RESERVE_ONLY")
		val reserveRelease = new NullableLocalDateTimeDatabaseField(self, "RESERVE_RELEASE")
	}

	def primaryKey: IntDatabaseField = fields.staggerId
}