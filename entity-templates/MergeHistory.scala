package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MergeHistory extends StorableClass(MergeHistory) {
	object values extends ValuesObject {
		val actionId = new IntFieldValue(self, MergeHistory.fields.actionId)
		val oldId = new NullableIntFieldValue(self, MergeHistory.fields.oldId)
		val newId = new NullableIntFieldValue(self, MergeHistory.fields.newId)
		val tableName = new NullableStringFieldValue(self, MergeHistory.fields.tableName)
		val columnName = new NullableStringFieldValue(self, MergeHistory.fields.columnName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, MergeHistory.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MergeHistory.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, MergeHistory.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MergeHistory.fields.updatedBy)
		val tablePk = new NullableDoubleFieldValue(self, MergeHistory.fields.tablePk)
	}
}

object MergeHistory extends StorableObject[MergeHistory] {
	val entityName: String = "MERGE_HISTORY"

	object fields extends FieldsObject {
		val actionId = new IntDatabaseField(self, "ACTION_ID")
		val oldId = new NullableIntDatabaseField(self, "OLD_ID")
		val newId = new NullableIntDatabaseField(self, "NEW_ID")
		val tableName = new NullableStringDatabaseField(self, "TABLE_NAME", 500)
		val columnName = new NullableStringDatabaseField(self, "COLUMN_NAME", 500)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val tablePk = new NullableDoubleDatabaseField(self, "TABLE_PK")
	}

	def primaryKey: IntDatabaseField = fields.actionId
}