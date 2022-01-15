package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class InstructionQueue extends StorableClass(InstructionQueue) {
	object values extends ValuesObject {
		val instructionId = new IntFieldValue(self, InstructionQueue.fields.instructionId)
		val cardNum = new StringFieldValue(self, InstructionQueue.fields.cardNum)
		val signoutId = new NullableIntFieldValue(self, InstructionQueue.fields.signoutId)
		val addedDatetime = new NullableLocalDateTimeFieldValue(self, InstructionQueue.fields.addedDatetime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, InstructionQueue.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, InstructionQueue.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, InstructionQueue.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, InstructionQueue.fields.updatedBy)
		val programId = new IntFieldValue(self, InstructionQueue.fields.programId)
		val queueOrder = new NullableDoubleFieldValue(self, InstructionQueue.fields.queueOrder)
		val personId = new IntFieldValue(self, InstructionQueue.fields.personId)
		val startActive = new NullableLocalDateTimeFieldValue(self, InstructionQueue.fields.startActive)
		val endActive = new NullableLocalDateTimeFieldValue(self, InstructionQueue.fields.endActive)
	}
}

object InstructionQueue extends StorableObject[InstructionQueue] {
	val entityName: String = "INSTRUCTION_QUEUE"

	object fields extends FieldsObject {
		val instructionId = new IntDatabaseField(self, "INSTRUCTION_ID")
		val cardNum = new StringDatabaseField(self, "CARD_NUM", 50)
		val signoutId = new NullableIntDatabaseField(self, "SIGNOUT_ID")
		val addedDatetime = new NullableLocalDateTimeDatabaseField(self, "ADDED_DATETIME")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val queueOrder = new NullableDoubleDatabaseField(self, "QUEUE_ORDER")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val startActive = new NullableLocalDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableLocalDateTimeDatabaseField(self, "END_ACTIVE")
	}

	def primaryKey: IntDatabaseField = fields.instructionId
}