package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ApClassInstance (
  instanceId: Int,
  formatId: Int,
  locationString: String
) extends StorableClass {
  def companion: StorableObject[ApClassInstance] = ApClassInstance
  object references extends ReferencesObject {
    var apClassFormat: Option[ApClassFormat] = None
  }

  def setApClassFormat(v: ApClassFormat): Unit = references.apClassFormat = Some(v)

  def getApClassFormat: ApClassFormat = references.apClassFormat match {
    case Some(x) => x
    case None => throw new Exception("ApClassFormat unset for ApClassInstance " + instanceId)
  }

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(ApClassInstance.fields.instanceId, instanceId),
    IntFieldValue(ApClassInstance.fields.formatId, formatId),
    StringFieldValue(ApClassInstance.fields.locationString, locationString)
  )
}

object ApClassInstance extends StorableObject[ApClassInstance] {
  val entityName: String = "AP_CLASS_INSTANCES"

  object fields extends FieldsObject {
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val formatId = new IntDatabaseField(self, "FORMAT_ID")
    val locationString = new StringDatabaseField(self, "LOCATION_STRING", 500)
  }

  val fieldList: List[DatabaseField] = List(
    fields.instanceId,
    fields.formatId,
    fields.locationString
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass =
    new ApClassInstance(
      r.intFields.get("INSTANCE_ID") match { case Some(Some(x)) => x; case _ => -1},
      r.intFields.get("FORMAT_ID") match { case Some(Some(x)) => x; case _ => -1 },
      r.stringFields.get("LOCATION_STRING") match { case Some(Some(x)) => x; case _ => "" }
    )

  def getSeedData: Set[ApClassInstance] = Set(
    ApClassInstance(1, 1, "Someplace")
  )
}
