package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassBookend extends StorableClass(JpClassBookend) {
	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, JpClassBookend.fields.instanceId)
		val firstSession = new DoubleFieldValue(self, JpClassBookend.fields.firstSession)
		val lastSession = new DoubleFieldValue(self, JpClassBookend.fields.lastSession)
	}
}

object JpClassBookend extends StorableObject[JpClassBookend] {
	val entityName: String = "JP_CLASS_BOOKENDS"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val firstSession = new DoubleDatabaseField(self, "FIRST_SESSION")
		val lastSession = new DoubleDatabaseField(self, "LAST_SESSION")
	}
}