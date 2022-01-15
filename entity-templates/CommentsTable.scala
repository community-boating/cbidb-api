package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CommentsTable extends StorableClass(CommentsTable) {
	object values extends ValuesObject {
		val tableName = new NullableStringFieldValue(self, CommentsTable.fields.tableName)
		val commentText = new NullableUnknownFieldType(self, CommentsTable.fields.commentText)
		val tableId = new IntFieldValue(self, CommentsTable.fields.tableId)
	}
}

object CommentsTable extends StorableObject[CommentsTable] {
	val entityName: String = "COMMENTS_TABLES"

	object fields extends FieldsObject {
		val tableName = new NullableStringDatabaseField(self, "TABLE_NAME", 50)
		val commentText = new NullableUnknownFieldType(self, "COMMENT_TEXT")
		val tableId = new IntDatabaseField(self, "TABLE_ID")
	}

	def primaryKey: IntDatabaseField = fields.tableId
}