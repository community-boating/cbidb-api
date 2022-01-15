package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EiiExtreme extends StorableClass(EiiExtreme) {
	object values extends ValuesObject {
		val eiiMin = new NullableDoubleFieldValue(self, EiiExtreme.fields.eiiMin)
		val eiiMax = new NullableDoubleFieldValue(self, EiiExtreme.fields.eiiMax)
	}
}

object EiiExtreme extends StorableObject[EiiExtreme] {
	val entityName: String = "EII_EXTREMES"

	object fields extends FieldsObject {
		val eiiMin = new NullableDoubleDatabaseField(self, "EII_MIN")
		val eiiMax = new NullableDoubleDatabaseField(self, "EII_MAX")
	}
}