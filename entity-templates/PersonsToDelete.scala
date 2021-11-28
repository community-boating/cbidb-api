package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsToDelete extends StorableClass(PersonsToDelete) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, PersonsToDelete.fields.rowId)
		val personId = new NullableIntFieldValue(self, PersonsToDelete.fields.personId)
		val mergedInto = new NullableDoubleFieldValue(self, PersonsToDelete.fields.mergedInto)
	}
}

object PersonsToDelete extends StorableObject[PersonsToDelete] {
	val entityName: String = "PERSONS_TO_DELETE"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val mergedInto = new NullableDoubleDatabaseField(self, "MERGED_INTO")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}