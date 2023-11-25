package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class InstructionQueue extends StorableClass(InstructionQueue) {
	override object values extends ValuesObject {
		val instructionId = new IntFieldValue(self, InstructionQueue.fields.instructionId)
		val cardNum = new StringFieldValue(self, InstructionQueue.fields.cardNum)
		val signoutId = new NullableIntFieldValue(self, InstructionQueue.fields.signoutId)
		val addedDatetime = new DateTimeFieldValue(self, InstructionQueue.fields.addedDatetime)
		val createdOn = new DateTimeFieldValue(self, InstructionQueue.fields.createdOn)
		val createdBy = new StringFieldValue(self, InstructionQueue.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, InstructionQueue.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, InstructionQueue.fields.updatedBy)
		val programId = new IntFieldValue(self, InstructionQueue.fields.programId)
		val queueOrder = new IntFieldValue(self, InstructionQueue.fields.queueOrder)
		val personId = new IntFieldValue(self, InstructionQueue.fields.personId)
		val startActive = new NullableDateTimeFieldValue(self, InstructionQueue.fields.startActive)
		val endActive = new NullableDateTimeFieldValue(self, InstructionQueue.fields.endActive)
	}
}

object InstructionQueue extends StorableObject[InstructionQueue] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "INSTRUCTION_QUEUE"

	object fields extends FieldsObject {
		val instructionId = new IntDatabaseField(self, "INSTRUCTION_ID")
		val cardNum = new StringDatabaseField(self, "CARD_NUM", 50)
		val signoutId = new NullableIntDatabaseField(self, "SIGNOUT_ID")
		@NullableInDatabase
		val addedDatetime = new DateTimeDatabaseField(self, "ADDED_DATETIME")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		@NullableInDatabase
		val queueOrder = new IntDatabaseField(self, "QUEUE_ORDER")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val startActive = new NullableDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableDateTimeDatabaseField(self, "END_ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.instructionId
}