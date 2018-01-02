package Entities.EntityDefinitions

import CbiUtil.{DefinedInitializable, Initializable}
import Services.RequestCache
import Storable.Fields.FieldValue.{DateTimeFieldValue, IntFieldValue}
import Storable.Fields.{DateTimeDatabaseField, IntDatabaseField}
import Storable._


class JpClassSession extends StorableClass {
  this.setCompanion(JpClassSession)
  object references extends ReferencesObject {
    var jpClassInstance = new Initializable[JpClassInstance]
  }
  object values extends ValuesObject {
    val sessionId = new IntFieldValue(self, JpClassSession.fields.sessionId)
    val instanceId = new IntFieldValue(self, JpClassSession.fields.instanceId)
    val sessionDateTime = new DateTimeFieldValue(self, JpClassSession.fields.sessionDateTime)
  }
  object calculatedValues extends CalculatedValuesObject {
    //lazy val jpWeek = new DefinedInitializable[RequestCache, Int]((rc: RequestCache) => rc.cachedEntities.membershipTypes)
  }
}

object JpClassSession extends StorableObject[JpClassSession] {
  val entityName: String = "JP_CLASS_SESSIONS"

  object fields extends FieldsObject {
    val sessionId = new IntDatabaseField(self, "SESSION_ID")
    val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
    val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
  }

  def primaryKey: IntDatabaseField = fields.sessionId
}