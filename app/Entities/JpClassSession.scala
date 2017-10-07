package Entities

import java.time.LocalDateTime

import Storable.Fields.FieldValue.FieldValue
import Storable.Fields.{DatabaseField, DateTimeDatabaseField, IntDatabaseField}
import Storable._


class JpClassSession extends StorableClass {
  def companion: StorableObject[JpClassSession] = JpClassSession
  object references extends ReferencesObject {
    var jpClassInstance: Option[JpClassInstance] = None
  }
  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")

  }

  def setJpClassInstance(v: JpClassInstance): Unit = references.jpClassInstance = Some(v)
  def getJpClassInstance: JpClassInstance = references.jpClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSession " + sessionId)
  }
}

object JpClassSession extends StorableObject[JpClassSession] {
  val entityName: String = "JP_CLASS_SESSIONS"

  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
  }

  val primaryKeyName: String = fields.sessionId.getFieldName


  def getSeedData: Set[JpClassSession] = Set()
}