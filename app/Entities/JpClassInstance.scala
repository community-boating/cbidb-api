package Entities

import Storable.Fields.FieldValue.IntFieldValue
import Storable.Fields.IntDatabaseField
import Storable._

class JpClassInstance extends StorableClass {
  def companion: StorableObject[JpClassInstance] = JpClassInstance
  object references extends ReferencesObject {
    var classLocation: Option[Option[ClassLocation]] = None
    var classInstructor: Option[Option[ClassInstructor]] = None
    var jpClassType: Option[JpClassType] = None
  }
  object values extends ValuesObject {
    val instanceId = new IntFieldValue(JpClassInstance.fields.instanceId)
    val instructorId = new IntFieldValue(JpClassInstance.fields.instructorId)
    val locationId = new IntFieldValue(JpClassInstance.fields.locationId)
    val typeId = new IntFieldValue(JpClassInstance.fields.typeId)
  }

  def setClassLocation(v: Option[ClassLocation]): Unit = references.classLocation = Some(v)
  def setClassInstructor(v: Option[ClassInstructor]): Unit = references.classInstructor = Some(v)
  def setJpClassType(v: JpClassType): Unit = references.jpClassType = Some(v)

  def getJpClassType: JpClassType = references.jpClassType match {
    case Some(x) => x
    case None => throw new Exception("JpClassType unset for JpClassInstance " + values.instanceId.get)
  }
}

object JpClassInstance extends StorableObject[JpClassInstance] {
  val entityName: String = "JP_CLASS_INSTANCES"

  object fields extends FieldsObject {
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
    val locationId = new IntDatabaseField(self, "LOCATION_ID")
    val typeId = new IntDatabaseField(self, "TYPE_ID")
  }

  val primaryKeyName: String = fields.instanceId.getFieldName

  def getSeedData: Set[JpClassInstance] = Set()
}
