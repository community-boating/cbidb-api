package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class TableInfo extends StorableClass(TableInfo) {
	override object values extends ValuesObject {
		val tableId = new IntFieldValue(self, TableInfo.fields.tableId)
		val tableName = new StringFieldValue(self, TableInfo.fields.tableName)
		val biTriggerName = new NullableStringFieldValue(self, TableInfo.fields.biTriggerName)
		val sequenceName = new NullableStringFieldValue(self, TableInfo.fields.sequenceName)
		val pkColumn = new NullableStringFieldValue(self, TableInfo.fields.pkColumn)
		val isLookup = new NullableBooleanFieldValue(self, TableInfo.fields.isLookup)
		val hasPersonId = new NullableBooleanFieldValue(self, TableInfo.fields.hasPersonId)
	}
}

object TableInfo extends StorableObject[TableInfo] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "TABLE_INFO"

	object fields extends FieldsObject {
		val tableId = new IntDatabaseField(self, "TABLE_ID")
		@NullableInDatabase
		val tableName = new StringDatabaseField(self, "TABLE_NAME", 100)
		val biTriggerName = new NullableStringDatabaseField(self, "BI_TRIGGER_NAME", 100)
		val sequenceName = new NullableStringDatabaseField(self, "SEQUENCE_NAME", 100)
		val pkColumn = new NullableStringDatabaseField(self, "PK_COLUMN", 100)
		val isLookup = new NullableBooleanDatabaseField(self, "IS_LOOKUP")
		val hasPersonId = new NullableBooleanDatabaseField(self, "HAS_PERSON_ID")
	}

	def primaryKey: IntDatabaseField = fields.tableId
}