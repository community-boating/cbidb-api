package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonRelationship extends StorableClass(PersonRelationship) {
	object values extends ValuesObject {
		val relationId = new IntFieldValue(self, PersonRelationship.fields.relationId)
		val a = new NullableDoubleFieldValue(self, PersonRelationship.fields.a)
		val b = new NullableDoubleFieldValue(self, PersonRelationship.fields.b)
		val typeId = new NullableIntFieldValue(self, PersonRelationship.fields.typeId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonRelationship.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonRelationship.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonRelationship.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonRelationship.fields.updatedBy)
	}
}

object PersonRelationship extends StorableObject[PersonRelationship] {
	val entityName: String = "PERSON_RELATIONSHIPS"

	object fields extends FieldsObject {
		val relationId = new IntDatabaseField(self, "RELATION_ID")
		val a = new NullableDoubleDatabaseField(self, "A")
		val b = new NullableDoubleDatabaseField(self, "B")
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.relationId
}