package Entities.Entities

import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._

class PersonRelationship extends StorableClass {
  this.setCompanion(PersonRelationship)
  object references extends ReferencesObject {
    var a: Option[Person] = None
    var b: Option[Person] = None
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