package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ZKeepalive extends StorableClass(ZKeepalive) {
	object values extends ValuesObject {
		val a = new NullableDoubleFieldValue(self, ZKeepalive.fields.a)
		val b = new NullableStringFieldValue(self, ZKeepalive.fields.b)
	}
}

object ZKeepalive extends StorableObject[ZKeepalive] {
	val entityName: String = "Z_KEEPALIVE"

	object fields extends FieldsObject {
		val a = new NullableDoubleDatabaseField(self, "A")
		val b = new NullableStringDatabaseField(self, "B")
	}
}