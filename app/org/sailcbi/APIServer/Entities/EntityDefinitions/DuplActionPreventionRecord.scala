package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class DuplActionPreventionRecord extends StorableClass(DuplActionPreventionRecord) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, DuplActionPreventionRecord.fields.id)
		val key = new StringFieldValue(self, DuplActionPreventionRecord.fields.key)
		val category = new StringFieldValue(self, DuplActionPreventionRecord.fields.category)
		val subjectSchema = new StringFieldValue(self, DuplActionPreventionRecord.fields.subjectSchema)
		val subjectId = new IntFieldValue(self, DuplActionPreventionRecord.fields.subjectId)
		val datetime = new DateTimeFieldValue(self, DuplActionPreventionRecord.fields.datetime)
		val status = new StringFieldValue(self, DuplActionPreventionRecord.fields.status)
	}
}

object DuplActionPreventionRecord extends StorableObject[DuplActionPreventionRecord] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DUPL_ACTION_PREVENTION_RECORDS"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		@NullableInDatabase
		val key = new StringDatabaseField(self, "KEY", 100)
		@NullableInDatabase
		val category = new StringDatabaseField(self, "CATEGORY", 50)
		@NullableInDatabase
		val subjectSchema = new StringDatabaseField(self, "SUBJECT_SCHEMA", 50)
		@NullableInDatabase
		val subjectId = new IntDatabaseField(self, "SUBJECT_ID")
		@NullableInDatabase
		val datetime = new DateTimeDatabaseField(self, "DATETIME")
		@NullableInDatabase
		val status = new StringDatabaseField(self, "STATUS", 1)
	}

	def primaryKey: IntDatabaseField = fields.id
}