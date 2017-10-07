package Entities

import Storable.Fields.FieldValue.FieldValue
import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class JpClassSignup (
  signupId: Int,
  instanceId: Int,
  signupType: String
) extends StorableClass {
  def companion: StorableObject[JpClassSignup] = JpClassSignup
  object references extends ReferencesObject {
    var jpClassInstance: Option[JpClassInstance] = None
  }
  def setJpClassInstance(v: JpClassInstance): Unit = references.jpClassInstance = Some(v)
  def getJpClassInstance: JpClassInstance = references.jpClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSignup " + signupId)
  }

  def deconstruct: Set[FieldValue] = Set(
    IntFieldValue(JpClassSignup.fields.signupId, signupId),
    IntFieldValue(JpClassSignup.fields.instanceId, instanceId),
    StringFieldValue(JpClassSignup.fields.signupType, signupType)
  )
}

object JpClassSignup extends StorableObject[JpClassSignup] {
  val entityName: String = "JP_CLASS_SIGNUPS"

  object fields extends FieldsObject {
    val signupId = new IntDatabaseField(self, "SIGNUP_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
  }

  val fieldList: List[DatabaseField[_]] = List(
    fields.signupId,
    fields.instanceId,
    fields.signupType
  )
  val primaryKeyName: String = fieldList.head.getFieldName

  def construct(r: DatabaseRow): ThisClass = {
    new JpClassSignup(
      fields.signupId.getValue(r),
      fields.instanceId.getValue(r),
      fields.signupType.getValue(r)
    )
  }

  def getSeedData: Set[JpClassSignup] = Set()
}