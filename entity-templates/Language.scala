package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Language extends StorableClass(Language) {
	object values extends ValuesObject {
		val languageId = new IntFieldValue(self, Language.fields.languageId)
		val languageName = new NullableStringFieldValue(self, Language.fields.languageName)
	}
}

object Language extends StorableObject[Language] {
	val entityName: String = "LANGUAGES"

	object fields extends FieldsObject {
		val languageId = new IntDatabaseField(self, "LANGUAGE_ID")
		val languageName = new NullableStringDatabaseField(self, "LANGUAGE_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.languageId
}