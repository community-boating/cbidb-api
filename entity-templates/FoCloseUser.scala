package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoCloseUser extends StorableClass(FoCloseUser) {
	object values extends ValuesObject {
		val closeId = new IntFieldValue(self, FoCloseUser.fields.closeId)
		val userId = new IntFieldValue(self, FoCloseUser.fields.userId)
		val openClose = new BooleanFIeldValue(self, FoCloseUser.fields.openClose)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoCloseUser.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoCloseUser.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoCloseUser.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoCloseUser.fields.updatedBy)
	}
}

object FoCloseUser extends StorableObject[FoCloseUser] {
	val entityName: String = "FO_CLOSE_USERS"

	object fields extends FieldsObject {
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val userId = new IntDatabaseField(self, "USER_ID")
		val openClose = new BooleanDatabaseField(self, "OPEN_CLOSE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.closeId
}