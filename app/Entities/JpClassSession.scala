package Entities

import Storable.Fields.FieldValue.{DateTimeFieldValue, IntFieldValue}
import Storable.Fields.{DateTimeDatabaseField, IntDatabaseField}
import Storable._


class JpClassSession extends StorableClass {
  object references extends ReferencesObject {
    var jpClassInstance: Option[JpClassInstance] = None
  }
  object values extends ValuesObject {
    val sessionId = new IntFieldValue(JpClassSession.fields.sessionId)
    val instanceId = new IntFieldValue(JpClassSession.fields.instanceId)
    val sessionDateTime = new DateTimeFieldValue(JpClassSession.fields.sessionDateTime)

  }

  def setJpClassInstance(v: JpClassInstance): Unit = references.jpClassInstance = Some(v)
  def getJpClassInstance: JpClassInstance = references.jpClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSession " + values.sessionId.get)
  }
}

object JpClassSession extends StorableObject[JpClassSession] {
  val entityName: String = "JP_CLASS_SESSIONS"

  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
  }

  val primaryKey: IntDatabaseField = fields.sessionId


  def getSeedData: Set[JpClassSession] = Set()
}