package Entities.Entities

import Storable.Fields.FieldValue.{IntFieldValue, StringFieldValue}
import Storable.Fields.{IntDatabaseField, StringDatabaseField}
import Storable._

class JpClassSignup extends StorableClass {
  this.setCompanion(JpClassSignup)
  object references extends ReferencesObject {
    var jpClassInstance: Option[JpClassInstance] = None
  }
  object values extends ValuesObject {
    val signupId = new IntFieldValue(self, JpClassSignup.fields.signupId)
    val instanceId = new IntFieldValue(self, JpClassSignup.fields.instanceId)
    val signupType = new StringFieldValue(self, JpClassSignup.fields.signupType)
  }

  def setJpClassInstance(v: JpClassInstance): Unit = references.jpClassInstance = Some(v)
  def getJpClassInstance: JpClassInstance = references.jpClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSignup " + values.signupId.get)
  }
}

object JpClassSignup extends StorableObject[JpClassSignup] {
  val entityName: String = "JP_CLASS_SIGNUPS"

  object fields extends FieldsObject {
    val signupId = new IntDatabaseField(self, "SIGNUP_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
  }

  def primaryKey: IntDatabaseField = fields.signupId
}