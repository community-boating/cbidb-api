package Entities

import Storable.Fields.FieldValue.{IntFieldValue, NullableStringFieldValue}
import Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import Storable._

class ApClassInstance extends StorableClass {
  def companion: StorableObject[ApClassInstance] = ApClassInstance
  object references extends ReferencesObject {
    var apClassFormat: Option[ApClassFormat] = None
  }

  object values extends ValuesObject {
    val instanceId = new IntFieldValue(ApClassInstance.fields.instanceId)
    val formatId = new IntFieldValue(ApClassInstance.fields.formatId)
    val locationString = new NullableStringFieldValue(ApClassInstance.fields.locationString)
  }

  def setApClassFormat(v: ApClassFormat): Unit = references.apClassFormat = Some(v)

  def getApClassFormat: ApClassFormat = references.apClassFormat match {
    case Some(x) => x
    case None => throw new Exception("ApClassFormat unset for ApClassInstance " + values.instanceId.get)
  }
}

object ApClassInstance extends StorableObject[ApClassInstance] {
  val entityName: String = "AP_CLASS_INSTANCES"

  object fields extends FieldsObject {
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val formatId = new IntDatabaseField(self, "FORMAT_ID")
    val locationString = new NullableStringDatabaseField(self, "LOCATION_STRING", 500)
  }

  val primaryKeyName: String = fields.instanceId.getFieldName

  def getSeedData: Set[ApClassInstance] = Set(
    // ApClassInstance(1, 1, Some("Someplace"))
  )
}
