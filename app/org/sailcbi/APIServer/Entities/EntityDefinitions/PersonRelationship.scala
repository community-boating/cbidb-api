package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.Storable.FieldValues.IntFieldValue
import org.sailcbi.APIServer.Storable.Fields.IntDatabaseField
import org.sailcbi.APIServer.Storable._

class PersonRelationship extends StorableClass {
	this.setCompanion(PersonRelationship)

	object references extends ReferencesObject {
		var a = new Initializable[Person]
		var b = new Initializable[Person]
	}

	object values extends ValuesObject {
		val relationId = new IntFieldValue(self, PersonRelationship.fields.relationId)
		val a = new IntFieldValue(self, PersonRelationship.fields.a)
		val b = new IntFieldValue(self, PersonRelationship.fields.b)
		val typeId = new IntFieldValue(self, PersonRelationship.fields.typeId)
	}

	override val valuesList = List(
		values.relationId,
		values.a,
		values.b,
		values.typeId
	)
}

object PersonRelationship extends StorableObject[PersonRelationship] {
	val entityName: String = "PERSON_RELATIONSHIPS"

	object fields extends FieldsObject {
		val relationId = new IntDatabaseField(self, "RELATION_ID")
		val a = new IntDatabaseField(self, "A")
		val b = new IntDatabaseField(self, "B")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
	}

	object specialIDs {
		val TYPE_ID_PARENT_CHILD_ACCT_LINKED: Int = 2
	}

	def primaryKey: IntDatabaseField = fields.relationId
}