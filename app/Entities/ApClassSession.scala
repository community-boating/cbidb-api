package Entities

import Storable.Fields.FieldValue.{DateTimeFieldValue, IntFieldValue}
import Storable.Fields.{DateTimeDatabaseField, IntDatabaseField}
import Storable._

class ApClassSession extends StorableClass {
  object references extends ReferencesObject {
    var apClassInstance: Option[ApClassInstance] = None
  }
  object values extends ValuesObject {
    val sessionId = new IntFieldValue(self, ApClassSession.fields.sessionId)
    val instanceId = new IntFieldValue(self, ApClassSession.fields.instanceId)
    val sessionDateTime = new DateTimeFieldValue(self, ApClassSession.fields.sessionDateTime)
  }

  def setApClassInstance(v: ApClassInstance): Unit = references.apClassInstance = Some(v)
  def getApClassInstance: ApClassInstance = references.apClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSession " + values.sessionId.get)
  }
}

object ApClassSession extends StorableObject[ApClassSession] {
  val entityName: String = "AP_CLASS_SESSIONS"

  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
  }

  val primaryKey: IntDatabaseField = fields.sessionId

  def getSeedData: Set[ApClassSession] = Set(
    //  ApClassSession(1, 1, LocalDateTime.now)
  )
}