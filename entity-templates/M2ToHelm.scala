package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class M2ToHelm extends StorableClass(M2ToHelm) {
	object values extends ValuesObject {
		val week = new NullableDoubleFieldValue(self, M2ToHelm.fields.week)
		val m2 = new DoubleFieldValue(self, M2ToHelm.fields.m2)
		val helms = new NullableDoubleFieldValue(self, M2ToHelm.fields.helms)
	}
}

object M2ToHelm extends StorableObject[M2ToHelm] {
	val entityName: String = "M2_TO_HELMS"

	object fields extends FieldsObject {
		val week = new NullableDoubleDatabaseField(self, "WEEK")
		val m2 = new DoubleDatabaseField(self, "M2")
		val helms = new NullableDoubleDatabaseField(self, "HELMS")
	}

	def primaryKey: IntDatabaseField = fields.m2
}