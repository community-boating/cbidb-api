package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EiiResponse extends StorableClass(EiiResponse) {
	override object values extends ValuesObject {
		val responseId = new IntFieldValue(self, EiiResponse.fields.responseId)
		val personId = new IntFieldValue(self, EiiResponse.fields.personId)
		val season = new DoubleFieldValue(self, EiiResponse.fields.season)
		val workers = new NullableDoubleFieldValue(self, EiiResponse.fields.workers)
		val benefits = new NullableBooleanFieldValue(self, EiiResponse.fields.benefits)
		val infants = new NullableDoubleFieldValue(self, EiiResponse.fields.infants)
		val preschoolers = new NullableDoubleFieldValue(self, EiiResponse.fields.preschoolers)
		val schoolagers = new NullableDoubleFieldValue(self, EiiResponse.fields.schoolagers)
		val teenagers = new NullableDoubleFieldValue(self, EiiResponse.fields.teenagers)
		val income = new NullableDoubleFieldValue(self, EiiResponse.fields.income)
		val computedEii = new NullableDoubleFieldValue(self, EiiResponse.fields.computedEii)
		val computedPrice = new DoubleFieldValue(self, EiiResponse.fields.computedPrice)
		val createdOn = new DateTimeFieldValue(self, EiiResponse.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EiiResponse.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, EiiResponse.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, EiiResponse.fields.updatedBy)
		val isApplying = new BooleanFieldValue(self, EiiResponse.fields.isApplying)
		val isCurrent = new BooleanFieldValue(self, EiiResponse.fields.isCurrent)
		val pdComment = new NullableUnknownFieldType(self, EiiResponse.fields.pdComment)
		val status = new NullableStringFieldValue(self, EiiResponse.fields.status)
		val utilizedInflationFactor = new NullableDoubleFieldValue(self, EiiResponse.fields.utilizedInflationFactor)
		val children = new NullableDoubleFieldValue(self, EiiResponse.fields.children)
		val nonworkingAdults = new NullableDoubleFieldValue(self, EiiResponse.fields.nonworkingAdults)
	}
}

object EiiResponse extends StorableObject[EiiResponse] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EII_RESPONSES"

	object fields extends FieldsObject {
		val responseId = new IntDatabaseField(self, "RESPONSE_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val workers = new NullableDoubleDatabaseField(self, "WORKERS")
		val benefits = new NullableBooleanDatabaseField(self, "BENEFITS")
		val infants = new NullableDoubleDatabaseField(self, "INFANTS")
		val preschoolers = new NullableDoubleDatabaseField(self, "PRESCHOOLERS")
		val schoolagers = new NullableDoubleDatabaseField(self, "SCHOOLAGERS")
		val teenagers = new NullableDoubleDatabaseField(self, "TEENAGERS")
		val income = new NullableDoubleDatabaseField(self, "INCOME")
		val computedEii = new NullableDoubleDatabaseField(self, "COMPUTED_EII")
		val computedPrice = new DoubleDatabaseField(self, "COMPUTED_PRICE")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val isApplying = new BooleanDatabaseField(self, "IS_APPLYING", false)
		val isCurrent = new BooleanDatabaseField(self, "IS_CURRENT", false)
		val pdComment = new NullableUnknownFieldType(self, "PD_COMMENT")
		val status = new NullableStringDatabaseField(self, "STATUS", 1)
		val utilizedInflationFactor = new NullableDoubleDatabaseField(self, "UTILIZED_INFLATION_FACTOR")
		val children = new NullableDoubleDatabaseField(self, "CHILDREN")
		val nonworkingAdults = new NullableDoubleDatabaseField(self, "NONWORKING_ADULTS")
	}

	def primaryKey: IntDatabaseField = fields.responseId
}