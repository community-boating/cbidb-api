package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EiiResponse extends StorableClass(EiiResponse) {
	object values extends ValuesObject {
		val responseId = new IntFieldValue(self, EiiResponse.fields.responseId)
		val personId = new IntFieldValue(self, EiiResponse.fields.personId)
		val season = new DoubleFieldValue(self, EiiResponse.fields.season)
		val workers = new NullableDoubleFieldValue(self, EiiResponse.fields.workers)
		val benefits = new NullableBooleanFIeldValue(self, EiiResponse.fields.benefits)
		val infants = new NullableDoubleFieldValue(self, EiiResponse.fields.infants)
		val preschoolers = new NullableDoubleFieldValue(self, EiiResponse.fields.preschoolers)
		val schoolagers = new NullableDoubleFieldValue(self, EiiResponse.fields.schoolagers)
		val teenagers = new NullableDoubleFieldValue(self, EiiResponse.fields.teenagers)
		val income = new NullableDoubleFieldValue(self, EiiResponse.fields.income)
		val computedEii = new NullableDoubleFieldValue(self, EiiResponse.fields.computedEii)
		val computedPrice = new NullableDoubleFieldValue(self, EiiResponse.fields.computedPrice)
		val createdOn = new NullableLocalDateTimeFieldValue(self, EiiResponse.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EiiResponse.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, EiiResponse.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, EiiResponse.fields.updatedBy)
		val isApplying = new NullableBooleanFIeldValue(self, EiiResponse.fields.isApplying)
		val isCurrent = new BooleanFIeldValue(self, EiiResponse.fields.isCurrent)
		val status = new NullableBooleanFIeldValue(self, EiiResponse.fields.status)
		val pdComment = new NullableUnknownFieldType(self, EiiResponse.fields.pdComment)
		val utilizedInflationFactor = new NullableDoubleFieldValue(self, EiiResponse.fields.utilizedInflationFactor)
		val children = new NullableDoubleFieldValue(self, EiiResponse.fields.children)
	}
}

object EiiResponse extends StorableObject[EiiResponse] {
	val entityName: String = "EII_RESPONSES"

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
		val computedPrice = new NullableDoubleDatabaseField(self, "COMPUTED_PRICE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val isApplying = new NullableBooleanDatabaseField(self, "IS_APPLYING")
		val isCurrent = new BooleanDatabaseField(self, "IS_CURRENT")
		val status = new NullableBooleanDatabaseField(self, "STATUS")
		val pdComment = new NullableUnknownFieldType(self, "PD_COMMENT")
		val utilizedInflationFactor = new NullableDoubleDatabaseField(self, "UTILIZED_INFLATION_FACTOR")
		val children = new NullableDoubleDatabaseField(self, "CHILDREN")
	}

	def primaryKey: IntDatabaseField = fields.responseId
}