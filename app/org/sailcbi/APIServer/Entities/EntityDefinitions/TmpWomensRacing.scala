package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class TmpWomensRacing extends StorableClass(TmpWomensRacing) {
	override object values extends ValuesObject {
		val email = new NullableStringFieldValue(self, TmpWomensRacing.fields.email)
	}
}

object TmpWomensRacing extends StorableObject[TmpWomensRacing] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "TMP_WOMENS_RACING"

	object fields extends FieldsObject {
		val email = new NullableStringDatabaseField(self, "EMAIL", 100)
	}
}