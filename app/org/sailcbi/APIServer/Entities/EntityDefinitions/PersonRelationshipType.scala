package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonRelationshipType extends StorableClass(PersonRelationshipType) {
	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, PersonRelationshipType.fields.typeId)
		val aTitle = new StringFieldValue(self, PersonRelationshipType.fields.aTitle)
		val bTitle = new StringFieldValue(self, PersonRelationshipType.fields.bTitle)
		val createdOn = new DateTimeFieldValue(self, PersonRelationshipType.fields.createdOn)
		val createdBy = new StringFieldValue(self, PersonRelationshipType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonRelationshipType.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, PersonRelationshipType.fields.updatedBy)
		val acctLink = new NullableBooleanFieldValue(self, PersonRelationshipType.fields.acctLink)
	}
}

object PersonRelationshipType extends StorableObject[PersonRelationshipType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSON_RELATIONSHIP_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val aTitle = new StringDatabaseField(self, "A_TITLE", 500)
		val bTitle = new StringDatabaseField(self, "B_TITLE", 500)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val acctLink = new NullableBooleanDatabaseField(self, "ACCT_LINK")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}