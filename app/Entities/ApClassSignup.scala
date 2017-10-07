package Entities

import Entities.ApClassSignup.self
import Storable.Fields.FieldValue.{FieldValue, IntFieldValue, StringFieldValue}
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

class ApClassSignup extends StorableClass {
  def companion: StorableObject[ApClassSignup] = ApClassSignup

  object references extends ReferencesObject {
    var apClassInstance: Option[ApClassSignup] = None
  }
  object values extends ValuesObject {
    val signupId = new IntFieldValue(ApClassSignup.fields.signupId)
    val instanceId = new IntFieldValue(ApClassSignup.fields.instanceId)
    val signupType = new StringFieldValue(ApClassSignup.fields.signupType)
  }

  def setJpClassInstance(v: ApClassSignup): Unit = references.apClassInstance = Some(v)
  def getJpClassInstance: ApClassSignup = references.apClassInstance match {
    case Some(x) => x
    case None => throw new Exception("ApClassInstance unset for ApClassSignup " + values.signupId.get)
  }
}

object ApClassSignup extends StorableObject[ApClassSignup] {
  val entityName: String = "AP_CLASS_SIGNUPS"

  object fields extends FieldsObject {
    val signupId = new IntDatabaseField(self, "SIGNUP_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
  }

  val primaryKeyName: String = fields.signupId.getFieldName

  def getSeedData: Set[ApClassSignup] = Set()
}