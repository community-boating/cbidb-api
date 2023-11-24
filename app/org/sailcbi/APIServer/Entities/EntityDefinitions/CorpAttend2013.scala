package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class CorpAttend2013 extends StorableClass(CorpAttend2013) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, CorpAttend2013.fields.id)
		val name = new NullableStringFieldValue(self, CorpAttend2013.fields.name)
		val personId = new NullableIntFieldValue(self, CorpAttend2013.fields.personId)
	}
}

object CorpAttend2013 extends StorableObject[CorpAttend2013] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CORP_ATTEND_2013"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		val name = new NullableStringDatabaseField(self, "NAME", 30)
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
	}

	def primaryKey: IntDatabaseField = fields.id
}