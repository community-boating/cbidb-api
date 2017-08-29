package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, NullableIntFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField}
import Storable._

case class JpClassInstance (
  instanceId: Int,
  instructorId: Option[Int],
  locationId: Option[Int],
  typeId: Int
) extends StorableClass {
  def companion: StorableObject[JpClassInstance] = JpClassInstance
  object references extends ReferencesObject {
    var classLocation: Option[Option[ClassLocation]] = None
    var classInstructor: Option[Option[ClassInstructor]] = None
    var jpClassType: Option[JpClassType] = None
  }

  def setClassLocation(v: Option[ClassLocation]): Unit = references.classLocation = Some(v)
  def setClassInstructor(v: Option[ClassInstructor]): Unit = references.classInstructor = Some(v)
  def setJpClassType(v: JpClassType): Unit = references.jpClassType = Some(v)

  def getJpClassType: JpClassType = references.jpClassType match {
    case Some(x) => x
    case None => throw new Exception("JpClassType unset for JpClassInstance " + instanceId)
  }

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(JpClassInstance.fields.instanceId, instanceId),
    NullableIntFieldValue(JpClassInstance.fields.instructorId, instructorId),
    NullableIntFieldValue(JpClassInstance.fields.locationId, locationId),
    IntFieldValue(JpClassInstance.fields.typeId, typeId)
  )
}

object JpClassInstance extends StorableObject[JpClassInstance] {
  val entityName: String = "JP_CLASS_INSTANCES"

  object fields extends FieldsObject {
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val instructorId = new IntDatabaseField(self, "INSTRUCTOR_ID")
    val locationId = new IntDatabaseField(self, "LOCATION_ID")
    val typeId = new IntDatabaseField(self, "TYPE_ID")
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.instanceId,
    fields.instructorId,
    fields.locationId,
    fields.typeId
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new JpClassInstance(
      fields.instanceId.getValue(r),
      fields.instructorId.getOptionValue(r),
      fields.locationId.getOptionValue(r),
      fields.typeId.getValue(r)
    )

  def getSeedData: Set[JpClassInstance] = Set()
}
