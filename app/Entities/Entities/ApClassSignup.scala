package Entities.Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class ApClassSignup extends StorableClass {
  this.setCompanion(ApClassSignup)
  object references extends ReferencesObject {
    var apClassInstance: Option[ApClassSignup] = None
  }
  object values extends ValuesObject {
    val signupId = new IntFieldValue(self, ApClassSignup.fields.signupId)
    val instanceId = new IntFieldValue(self, ApClassSignup.fields.instanceId)
    val signupType = new StringFieldValue(self, ApClassSignup.fields.signupType)
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

  def primaryKey: IntDatabaseField = fields.signupId
}