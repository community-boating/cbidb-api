package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassBookendsOld extends StorableClass(JpClassBookendsOld) {
	object values extends ValuesObject {
		val instanceId = new IntFieldValue(self, JpClassBookendsOld.fields.instanceId)
		val firstSession = new DoubleFieldValue(self, JpClassBookendsOld.fields.firstSession)
		val lastSession = new DoubleFieldValue(self, JpClassBookendsOld.fields.lastSession)
	}
}

object JpClassBookendsOld extends StorableObject[JpClassBookendsOld] {
	val entityName: String = "JP_CLASS_BOOKENDS_OLD"

	object fields extends FieldsObject {
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val firstSession = new DoubleDatabaseField(self, "FIRST_SESSION")
		val lastSession = new DoubleDatabaseField(self, "LAST_SESSION")
	}
}