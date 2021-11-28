package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonRelationshipType extends StorableClass(PersonRelationshipType) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, PersonRelationshipType.fields.typeId)
		val aTitle = new NullableStringFieldValue(self, PersonRelationshipType.fields.aTitle)
		val bTitle = new NullableStringFieldValue(self, PersonRelationshipType.fields.bTitle)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonRelationshipType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonRelationshipType.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonRelationshipType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonRelationshipType.fields.updatedBy)
		val acctLink = new NullableBooleanFIeldValue(self, PersonRelationshipType.fields.acctLink)
	}
}

object PersonRelationshipType extends StorableObject[PersonRelationshipType] {
	val entityName: String = "PERSON_RELATIONSHIP_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val aTitle = new NullableStringDatabaseField(self, "A_TITLE", 500)
		val bTitle = new NullableStringDatabaseField(self, "B_TITLE", 500)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val acctLink = new NullableBooleanDatabaseField(self, "ACCT_LINK")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}