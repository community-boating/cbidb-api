package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class CommentsTable extends StorableClass(CommentsTable) {
	override object values extends ValuesObject {
		val tableId = new IntFieldValue(self, CommentsTable.fields.tableId)
		val tableName = new StringFieldValue(self, CommentsTable.fields.tableName)
		val commentText = new NullableStringFieldValue(self, CommentsTable.fields.commentText)
	}
}

object CommentsTable extends StorableObject[CommentsTable] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "COMMENTS_TABLES"

	object fields extends FieldsObject {
		val tableId = new IntDatabaseField(self, "TABLE_ID")
		@NullableInDatabase
		val tableName = new StringDatabaseField(self, "TABLE_NAME", 50)
		val commentText = new NullableStringDatabaseField(self, "COMMENT_TEXT", -1)
	}

	def primaryKey: IntDatabaseField = fields.tableId
}