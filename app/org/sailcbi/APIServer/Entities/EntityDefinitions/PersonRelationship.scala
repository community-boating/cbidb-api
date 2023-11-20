package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonRelationship extends StorableClass(PersonRelationship) {
	override object references extends ReferencesObject {
		val a = new Initializable[Person]
		val b = new Initializable[Person]
	}

	override object values extends ValuesObject {
		val relationId = new IntFieldValue(self, PersonRelationship.fields.relationId)
		val a = new DoubleFieldValue(self, PersonRelationship.fields.a)
		val b = new DoubleFieldValue(self, PersonRelationship.fields.b)
		val typeId = new IntFieldValue(self, PersonRelationship.fields.typeId)
		val createdOn = new DateTimeFieldValue(self, PersonRelationship.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonRelationship.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonRelationship.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonRelationship.fields.updatedBy)
	}
}

object PersonRelationship extends StorableObject[PersonRelationship] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSON_RELATIONSHIPS"

	object fields extends FieldsObject {
		val relationId = new IntDatabaseField(self, "RELATION_ID")
		val a = new DoubleDatabaseField(self, "A")
		val b = new DoubleDatabaseField(self, "B")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.relationId
}