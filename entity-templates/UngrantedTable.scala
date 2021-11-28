package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UngrantedTable extends StorableClass(UngrantedTable) {
	object values extends ValuesObject {
		val tableName = new NullableStringFieldValue(self, UngrantedTable.fields.tableName)
	}
}

object UngrantedTable extends StorableObject[UngrantedTable] {
	val entityName: String = "UNGRANTED_TABLES"

	object fields extends FieldsObject {
		val tableName = new NullableStringDatabaseField(self, "TABLE_NAME", 128)
	}
}