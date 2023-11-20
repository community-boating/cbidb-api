package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class TempMakeIndice extends StorableClass(TempMakeIndice) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, TempMakeIndice.fields.id)
		val tableName = new NullableStringFieldValue(self, TempMakeIndice.fields.tableName)
		val fkColumn = new NullableStringFieldValue(self, TempMakeIndice.fields.fkColumn)
	}
}

object TempMakeIndice extends StorableObject[TempMakeIndice] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "TEMP_MAKE_INDICES"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		val tableName = new NullableStringDatabaseField(self, "TABLE_NAME", 30)
		val fkColumn = new NullableStringDatabaseField(self, "FK_COLUMN", 30)
	}

	def primaryKey: IntDatabaseField = fields.id
}