package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CommentsTable extends StorableClass(CommentsTable) {
	override object values extends ValuesObject {
		val tableId = new IntFieldValue(self, CommentsTable.fields.tableId)
		val tableName = new StringFieldValue(self, CommentsTable.fields.tableName)
		val commentText = new NullableUnknownFieldType(self, CommentsTable.fields.commentText)
	}
}

object CommentsTable extends StorableObject[CommentsTable] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "COMMENTS_TABLES"

	object fields extends FieldsObject {
		val tableId = new IntDatabaseField(self, "TABLE_ID")
		val tableName = new StringDatabaseField(self, "TABLE_NAME", 50)
		val commentText = new NullableUnknownFieldType(self, "COMMENT_TEXT")
	}

	def primaryKey: IntDatabaseField = fields.tableId
}