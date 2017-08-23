package Entities

import Storable.Fields.{DatabaseField, IntDatabaseField, StringDatabaseField}
import Storable._

case class JpClassSignup (
  signupId: Int,
  instanceId: Int,
  signupType: String
) extends StorableClass {
  object references extends ReferencesObject {
    var jpClassInstance: Option[JpClassInstance] = None
  }
  def setJpClassInstance(v: JpClassInstance): Unit = references.jpClassInstance = Some(v)
  def getJpClassInstance: JpClassInstance = references.jpClassInstance match {
    case Some(x) => x
    case None => throw new Exception("JpClassInstance unset for JpClassSignup " + signupId)
  }
}

object JpClassSignup extends StorableObject[JpClassSignup] {
  val entityName: String = "JP_CLASS_SIGNUPS"

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
    new JpClassSignup(
      r.intFields.get("SIGNUP_ID") match { case Some(Some(x)) => x; case _ => -1 },
      r.intFields.get("INSTANCE_ID") match { case Some(Some(x)) => x; case _ => -1 },
      r.stringFields.get("SIGNUP_TYPE") match { case Some(Some(x)) => x; case _ => "" }
    )
  }

  def getTestData: Set[JpClassSignup] = Set()
}