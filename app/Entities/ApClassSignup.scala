package Entities

import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class ApClassSignup (
  signupId: Int,
  instanceId: Int,
  signupType: String
) extends StorableClass {
  def companion: StorableObject[ApClassSignup] = ApClassSignup
  object references extends ReferencesObject {
    var apClassInstance: Option[ApClassSignup] = None
  }
  def setJpClassInstance(v: ApClassSignup): Unit = references.apClassInstance = Some(v)
  def getJpClassInstance: ApClassSignup = references.apClassInstance match {
    case Some(x) => x
    case None => throw new Exception("ApClassInstance unset for ApClassSignup " + signupId)
  }

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(ApClassSignup.fields.signupId, signupId),
    IntFieldValue(ApClassSignup.fields.instanceId, instanceId),
    StringFieldValue(ApClassSignup.fields.signupType, signupType)
  )
}

object ApClassSignup extends StorableObject[ApClassSignup] {
  val entityName: String = "AP_CLASS_SIGNUPS"

  object fields extends FieldsObject {
    val signupId = new IntDatabaseField(self, "SIGNUP_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
  }

  val fieldList: List[DatabaseField] = List(
    fields.signupId,
    fields.instanceId,
    fields.signupType
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass = {
    new ApClassSignup(
      r.intFields.get("SIGNUP_ID") match { case Some(Some(x)) => x; case _ => -1 },
      r.intFields.get("INSTANCE_ID") match { case Some(Some(x)) => x; case _ => -1 },
      r.stringFields.get("SIGNUP_TYPE") match { case Some(Some(x)) => x; case _ => "" }
    )
  }

  def getSeedData: Set[ApClassSignup] = Set()
}