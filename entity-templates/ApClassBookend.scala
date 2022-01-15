package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassBookend extends StorableClass(ApClassBookend) {
	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, ApClassBookend.fields.instanceId)
		val firstSession = new DoubleFieldValue(self, ApClassBookend.fields.firstSession)
		val lastSession = new DoubleFieldValue(self, ApClassBookend.fields.lastSession)
	}
}

object ApClassBookend extends StorableObject[ApClassBookend] {
	val entityName: String = "AP_CLASS_BOOKENDS"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val firstSession = new DoubleDatabaseField(self, "FIRST_SESSION")
		val lastSession = new DoubleDatabaseField(self, "LAST_SESSION")
	}
}