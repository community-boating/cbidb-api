package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.IntFieldValue
import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable

class PersonRelationship extends StorableClass(PersonRelationship) {
	override object references extends ReferencesObject {
		val a = new Initializable[Person]
		val b = new Initializable[Person]
	}

	object values extends ValuesObject {
		val relationId = new IntFieldValue(self, PersonRelationship.fields.relationId)
		val a = new IntFieldValue(self, PersonRelationship.fields.a)
		val b = new IntFieldValue(self, PersonRelationship.fields.b)
		val typeId = new IntFieldValue(self, PersonRelationship.fields.typeId)
	}
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