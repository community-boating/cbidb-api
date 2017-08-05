package Entities

import java.time.LocalDateTime

import Storable.Fields.{DatabaseField, DateTimeDatabaseField, IntDatabaseField}
import Storable._


case class ApClassSession(
  sessionId: Int,
  instanceId: Int,
  sessionDateTime: LocalDateTime
) extends StorableClass {
  object references extends ReferencesObject {
    var apClassInstance: Option[ApClassInstance] = None
  }
  def setApClassInstance(v: ApClassInstance): Unit = references.apClassInstance = Some(v)
  def getApClassInstance: ApClassInstance = references.apClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSession " + sessionId)
  }
}

object ApClassSession extends StorableObject[ApClassSession] {
  val entityName: String = "AP_CLASS_SESSIONS"

  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
  }

  val fieldList: List[DatabaseField] = List(
    fields.sessionId,
    fields.instanceId,
    fields.sessionDateTime
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass = {
    //println(r.intFields)
    new ApClassSession(
      r.intFields.get("SESSION_ID") match { case Some(Some(x)) => x; case _ => -1 },
      r.intFields.get("INSTANCE_ID") match { case Some(Some(x)) => x; case _ => -1 },
      r.dateTimeFields.get("SESSION_DATETIME") match { case Some(Some(x)) => x; case _ => LocalDateTime.now }
    )
  }
}