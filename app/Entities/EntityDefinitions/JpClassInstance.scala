package Entities.EntityDefinitions

import CbiUtil.Initializable
import Storable.Fields.FieldValue.{IntFieldValue, NullableIntFieldValue}
import Storable.Fields.{IntDatabaseField, NullableIntDatabaseField}
import Storable._

class JpClassInstance extends StorableClass {
  this.setCompanion(JpClassInstance)
  object references extends ReferencesObject {
    var classLocation = new Initializable[Option[ClassLocation]]
    var classInstructor = new Initializable[Option[ClassInstructor]]
    var jpClassType = new Initializable[JpClassType]
  }
  object values extends ValuesObject {
    val instanceId = new IntFieldValue(self, JpClassInstance.fields.instanceId)
    val instructorId = new NullableIntFieldValue(self, JpClassInstance.fields.instructorId)
    val locationId = new NullableIntFieldValue(self, JpClassInstance.fields.locationId)
    val typeId = new IntFieldValue(self, JpClassInstance.fields.typeId)
  }
}

object JpClassInstance extends StorableObject[JpClassInstance] {
  val entityName: String = "JP_CLASS_INSTANCES"

  object fields extends FieldsObject {
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val instructorId = new NullableIntDatabaseField(self, "INSTRUCTOR_ID")
    val locationId = new NullableIntDatabaseField(self, "LOCATION_ID")
    val typeId = new IntDatabaseField(self, "TYPE_ID")
  }

  def primaryKey: IntDatabaseField = fields.instanceId
}
