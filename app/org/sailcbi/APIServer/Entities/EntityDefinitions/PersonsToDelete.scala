package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsToDelete extends StorableClass(PersonsToDelete) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, PersonsToDelete.fields.rowId)
		val personId = new IntFieldValue(self, PersonsToDelete.fields.personId)
		val mergedInto = new DoubleFieldValue(self, PersonsToDelete.fields.mergedInto)
	}
}

object PersonsToDelete extends StorableObject[PersonsToDelete] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_TO_DELETE"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val mergedInto = new DoubleDatabaseField(self, "MERGED_INTO")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}