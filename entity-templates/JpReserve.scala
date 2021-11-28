package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpReserve extends StorableClass(JpReserve) {
	object values extends ValuesObject {
		val failure = new DoubleFieldValue(self, JpReserve.fields.failure)
		val reserve = new DoubleFieldValue(self, JpReserve.fields.reserve)
	}
}

object JpReserve extends StorableObject[JpReserve] {
	val entityName: String = "JP_RESERVES"

	object fields extends FieldsObject {
		val failure = new DoubleDatabaseField(self, "FAILURE")
		val reserve = new DoubleDatabaseField(self, "RESERVE")
	}
}