package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class CommentsColumn extends StorableClass(CommentsColumn) {
	override object values extends ValuesObject {
		val columnId = new IntFieldValue(self, CommentsColumn.fields.columnId)
		val columnName = new StringFieldValue(self, CommentsColumn.fields.columnName)
		val commentText = new NullableStringFieldValue(self, CommentsColumn.fields.commentText)
		val tableId = new IntFieldValue(self, CommentsColumn.fields.tableId)
	}
}

object CommentsColumn extends StorableObject[CommentsColumn] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "COMMENTS_COLUMNS"

	object fields extends FieldsObject {
		val columnId = new IntDatabaseField(self, "COLUMN_ID")
		@NullableInDatabase
		val columnName = new StringDatabaseField(self, "COLUMN_NAME", 50)
		val commentText = new NullableStringDatabaseField(self, "COMMENT_TEXT", -1)
		val tableId = new IntDatabaseField(self, "TABLE_ID")
	}

	def primaryKey: IntDatabaseField = fields.columnId
}