package Entities

import java.time.LocalDateTime

import Storable.Fields.FieldValue.{DateTimeFieldValue, FieldValue, IntFieldValue}
import Storable.Fields.{DatabaseField, DateTimeDatabaseField, IntDatabaseField}
import Storable._


case class ApClassSession(
  sessionId: Int,
  instanceId: Int,
  sessionDateTime: LocalDateTime
) extends StorableClass {
  def companion: StorableObject[ApClassSession] = ApClassSession
  object references extends ReferencesObject {
    var apClassInstance: Option[ApClassInstance] = None
  }
  def setApClassInstance(v: ApClassInstance): Unit = references.apClassInstance = Some(v)
  def getApClassInstance: ApClassInstance = references.apClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSession " + sessionId)
  }

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(ApClassSession.fields.sessionId, sessionId),
    IntFieldValue(ApClassSession.fields.instanceId, instanceId),
    DateTimeFieldValue(ApClassSession.fields.sessionDateTime, sessionDateTime)
  )
}

object ApClassSession extends StorableObject[ApClassSession] {
  val entityName: String = "AP_CLASS_SESSIONS"

  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.sessionId,
    fields.instanceId,
    fields.sessionDateTime
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass = {
    new ApClassSession(
      fields.sessionId.getValue(r),
      fields.instanceId.getValue(r),
      fields.sessionDateTime.getValue(r)
    )
  }

  def getSeedData: Set[ApClassSession] = Set(
    ApClassSession(1, 1, LocalDateTime.now)
  )
}