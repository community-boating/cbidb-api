package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SequenceInfo extends StorableClass(SequenceInfo) {
	object values extends ValuesObject {
		val sequenceName = new NullableStringFieldValue(self, SequenceInfo.fields.sequenceName)
		val tableName = new StringFieldValue(self, SequenceInfo.fields.tableName)
		val columnName = new NullableStringFieldValue(self, SequenceInfo.fields.columnName)
		val position = new NullableDoubleFieldValue(self, SequenceInfo.fields.position)
	}
}

object SequenceInfo extends StorableObject[SequenceInfo] {
	val entityName: String = "SEQUENCE_INFO"

	object fields extends FieldsObject {
		val sequenceName = new NullableStringDatabaseField(self, "SEQUENCE_NAME", 128)
		val tableName = new StringDatabaseField(self, "TABLE_NAME", 128)
		val columnName = new NullableStringDatabaseField(self, "COLUMN_NAME", 4000)
		val position = new NullableDoubleDatabaseField(self, "POSITION")
	}
}