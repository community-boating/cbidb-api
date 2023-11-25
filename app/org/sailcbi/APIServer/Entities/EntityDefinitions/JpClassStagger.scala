package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpClassStagger extends StorableClass(JpClassStagger) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
	}

	override object values extends ValuesObject {
		val staggerId = new IntFieldValue(self, JpClassStagger.fields.staggerId)
		val instanceId = new IntFieldValue(self, JpClassStagger.fields.instanceId)
		val staggerDate = new DateTimeFieldValue(self, JpClassStagger.fields.staggerDate)
		val occupancy = new IntFieldValue(self, JpClassStagger.fields.occupancy)
		val createdOn = new DateTimeFieldValue(self, JpClassStagger.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassStagger.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassStagger.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassStagger.fields.updatedBy)
		val reserveOnly = new NullableBooleanFieldValue(self, JpClassStagger.fields.reserveOnly)
		val reserveRelease = new NullableDateTimeFieldValue(self, JpClassStagger.fields.reserveRelease)
	}
}

object JpClassStagger extends StorableObject[JpClassStagger] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_STAGGERS"

	object fields extends FieldsObject {
		val staggerId = new IntDatabaseField(self, "STAGGER_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val staggerDate = new DateTimeDatabaseField(self, "STAGGER_DATE")
		val occupancy = new IntDatabaseField(self, "OCCUPANCY")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val reserveOnly = new NullableBooleanDatabaseField(self, "RESERVE_ONLY")
		val reserveRelease = new NullableDateTimeDatabaseField(self, "RESERVE_RELEASE")
	}

	def primaryKey: IntDatabaseField = fields.staggerId
}