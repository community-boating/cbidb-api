package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class MergeHistory extends StorableClass(MergeHistory) {
	override object values extends ValuesObject {
		val actionId = new IntFieldValue(self, MergeHistory.fields.actionId)
		val oldId = new IntFieldValue(self, MergeHistory.fields.oldId)
		val newId = new IntFieldValue(self, MergeHistory.fields.newId)
		val tableName = new StringFieldValue(self, MergeHistory.fields.tableName)
		val columnName = new StringFieldValue(self, MergeHistory.fields.columnName)
		val createdOn = new DateTimeFieldValue(self, MergeHistory.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, MergeHistory.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, MergeHistory.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, MergeHistory.fields.updatedBy)
		val tablePk = new NullableDoubleFieldValue(self, MergeHistory.fields.tablePk)
	}
}

object MergeHistory extends StorableObject[MergeHistory] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MERGE_HISTORY"

	object fields extends FieldsObject {
		val actionId = new IntDatabaseField(self, "ACTION_ID")
		@NullableInDatabase
		val oldId = new IntDatabaseField(self, "OLD_ID")
		@NullableInDatabase
		val newId = new IntDatabaseField(self, "NEW_ID")
		@NullableInDatabase
		val tableName = new StringDatabaseField(self, "TABLE_NAME", 500)
		@NullableInDatabase
		val columnName = new StringDatabaseField(self, "COLUMN_NAME", 500)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val tablePk = new NullableDoubleDatabaseField(self, "TABLE_PK")
	}

	def primaryKey: IntDatabaseField = fields.actionId
}