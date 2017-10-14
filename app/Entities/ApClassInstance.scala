package Entities

import Reporting.ReportableStorableClass
import Services.PersistenceBroker
import Storable.Fields.FieldValue.{IntFieldValue, NullableStringFieldValue}
import Storable.Fields.{IntDatabaseField, NullableStringDatabaseField}
import Storable._

class ApClassInstance extends ReportableStorableClass {
  this.setCompanion(ApClassInstance)
  object references extends ReferencesObject {
    var apClassFormat: Option[ApClassFormat] = None
  }

  object values extends ValuesObject {
    val instanceId = new IntFieldValue(self, ApClassInstance.fields.instanceId)
    val formatId = new IntFieldValue(self, ApClassInstance.fields.formatId)
    val locationString = new NullableStringFieldValue(self, ApClassInstance.fields.locationString)
  }

  def setApClassFormat(v: ApClassFormat): Unit = references.apClassFormat = Some(v)

  def getApClassFormat: ApClassFormat = references.apClassFormat match {
    case Some(x) => x
    case None => throw new Exception("ApClassFormat unset for ApClassInstance " + values.instanceId.get)
  }

  def getApClassSessions(pb: PersistenceBroker): List[ApClassSession] = {
    val id = values.instanceId.get
    pb.getObjectsByFilters(
      ApClassSession,
      List(ApClassSession.fields.instanceId.equalsConstant(id)),
      5
    )
  }
}

object ApClassInstance extends StorableObject[ApClassInstance] {
  val entityName: String = "AP_CLASS_INSTANCES"

  object fields extends FieldsObject {
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val formatId = new IntDatabaseField(self, "FORMAT_ID")
    val locationString = new NullableStringDatabaseField(self, "LOCATION_STRING", 500)
  }

  def primaryKey: IntDatabaseField = fields.instanceId

  def getSeedData: Set[ApClassInstance] = Set(
    // ApClassInstance(1, 1, Some("Someplace"))
  )
}
