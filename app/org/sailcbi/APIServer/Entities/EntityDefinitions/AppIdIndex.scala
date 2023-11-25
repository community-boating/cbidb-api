package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class AppIdIndex extends StorableClass(AppIdIndex) {
	override object values extends ValuesObject {
		val indexId = new IntFieldValue(self, AppIdIndex.fields.indexId)
		val appAlias = new NullableStringFieldValue(self, AppIdIndex.fields.appAlias)
		val appId = new NullableIntFieldValue(self, AppIdIndex.fields.appId)
		val createdOn = new NullableDateTimeFieldValue(self, AppIdIndex.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, AppIdIndex.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, AppIdIndex.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, AppIdIndex.fields.updatedBy)
		val appName = new NullableStringFieldValue(self, AppIdIndex.fields.appName)
	}
}

object AppIdIndex extends StorableObject[AppIdIndex] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "APP_ID_INDEX"

	object fields extends FieldsObject {
		val indexId = new IntDatabaseField(self, "INDEX_ID")
		val appAlias = new NullableStringDatabaseField(self, "APP_ALIAS", 200)
		val appId = new NullableIntDatabaseField(self, "APP_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val appName = new NullableStringDatabaseField(self, "APP_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.indexId
}