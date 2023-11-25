package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class Language extends StorableClass(Language) {
	override object values extends ValuesObject {
		val languageId = new IntFieldValue(self, Language.fields.languageId)
		val languageName = new StringFieldValue(self, Language.fields.languageName)
	}
}

object Language extends StorableObject[Language] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "LANGUAGES"

	object fields extends FieldsObject {
		val languageId = new IntDatabaseField(self, "LANGUAGE_ID")
		@NullableInDatabase
		val languageName = new StringDatabaseField(self, "LANGUAGE_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.languageId
}