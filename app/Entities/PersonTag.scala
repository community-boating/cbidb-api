package Entities

import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._

class PersonTag extends StorableClass {
  this.setCompanion(PersonTag)
  object references extends ReferencesObject {
    var person: Option[Person] = None
    var tag: Option[Tag] = None
  }
  object values extends ValuesObject {
    val assignId = new IntFieldValue(self, PersonTag.fields.assignId)
    val personId = new IntFieldValue(self, PersonTag.fields.personId)
    val tagId = new IntFieldValue(self, PersonTag.fields.tagId)
  }
}

object PersonTag extends StorableObject[PersonTag] {
  val entityName: String = "PERSONS_TAGS"

  object fields extends FieldsObject {
    val assignId = new IntDatabaseField(self, "ASSIGN_ID")
    val personId = new IntDatabaseField(self, "PERSON_ID")
    val tagId = new IntDatabaseField(self, "TAG_ID")
  }

  def primaryKey: IntDatabaseField = fields.assignId
}