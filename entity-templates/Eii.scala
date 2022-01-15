package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Eii extends StorableClass(Eii) {
	object values extends ValuesObject {
		val id = new IntFieldValue(self, Eii.fields.id)
		val desc7 = new NullableStringFieldValue(self, Eii.fields.desc7)
		val desc6 = new NullableStringFieldValue(self, Eii.fields.desc6)
		val desc5 = new NullableStringFieldValue(self, Eii.fields.desc5)
		val desc4 = new NullableStringFieldValue(self, Eii.fields.desc4)
		val desc3 = new NullableStringFieldValue(self, Eii.fields.desc3)
		val desc2 = new NullableStringFieldValue(self, Eii.fields.desc2)
		val desc1 = new NullableStringFieldValue(self, Eii.fields.desc1)
		val housing = new NullableDoubleFieldValue(self, Eii.fields.housing)
		val utilities = new NullableDoubleFieldValue(self, Eii.fields.utilities)
		val food = new NullableDoubleFieldValue(self, Eii.fields.food)
		val transportation = new NullableDoubleFieldValue(self, Eii.fields.transportation)
		val childCare = new NullableDoubleFieldValue(self, Eii.fields.childCare)
		val personalHouseholdNeeds = new NullableDoubleFieldValue(self, Eii.fields.personalHouseholdNeeds)
		val healthcare = new NullableDoubleFieldValue(self, Eii.fields.healthcare)
		val taxes = new NullableDoubleFieldValue(self, Eii.fields.taxes)
		val taxCredits = new NullableDoubleFieldValue(self, Eii.fields.taxCredits)
		val monthlyTotal = new NullableDoubleFieldValue(self, Eii.fields.monthlyTotal)
		val annualTotal = new NullableDoubleFieldValue(self, Eii.fields.annualTotal)
		val hourlyWage = new NullableDoubleFieldValue(self, Eii.fields.hourlyWage)
		val precautionary = new NullableDoubleFieldValue(self, Eii.fields.precautionary)
		val retirement = new NullableDoubleFieldValue(self, Eii.fields.retirement)
		val childrensEducationTraining = new NullableDoubleFieldValue(self, Eii.fields.childrensEducationTraining)
		val homeownership = new NullableDoubleFieldValue(self, Eii.fields.homeownership)
		val benefits = new NullableStringFieldValue(self, Eii.fields.benefits)
		val monthlyTotalTemp = new NullableStringFieldValue(self, Eii.fields.monthlyTotalTemp)
		val annualTotalTemp = new NullableStringFieldValue(self, Eii.fields.annualTotalTemp)
		val workerCt = new NullableDoubleFieldValue(self, Eii.fields.workerCt)
		val infantCt = new NullableDoubleFieldValue(self, Eii.fields.infantCt)
		val preschoolerCt = new NullableDoubleFieldValue(self, Eii.fields.preschoolerCt)
		val schoolageCt = new NullableDoubleFieldValue(self, Eii.fields.schoolageCt)
		val teenageCt = new NullableDoubleFieldValue(self, Eii.fields.teenageCt)
	}
}

object Eii extends StorableObject[Eii] {
	val entityName: String = "EII"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		val desc7 = new NullableStringDatabaseField(self, "DESC_7", 30)
		val desc6 = new NullableStringDatabaseField(self, "DESC_6", 30)
		val desc5 = new NullableStringDatabaseField(self, "DESC_5", 30)
		val desc4 = new NullableStringDatabaseField(self, "DESC_4", 30)
		val desc3 = new NullableStringDatabaseField(self, "DESC_3", 30)
		val desc2 = new NullableStringDatabaseField(self, "DESC_2", 30)
		val desc1 = new NullableStringDatabaseField(self, "DESC_1", 30)
		val housing = new NullableDoubleDatabaseField(self, "HOUSING")
		val utilities = new NullableDoubleDatabaseField(self, "UTILITIES")
		val food = new NullableDoubleDatabaseField(self, "FOOD")
		val transportation = new NullableDoubleDatabaseField(self, "TRANSPORTATION")
		val childCare = new NullableDoubleDatabaseField(self, "CHILD_CARE")
		val personalHouseholdNeeds = new NullableDoubleDatabaseField(self, "PERSONAL_HOUSEHOLD_NEEDS")
		val healthcare = new NullableDoubleDatabaseField(self, "HEALTHCARE")
		val taxes = new NullableDoubleDatabaseField(self, "TAXES")
		val taxCredits = new NullableDoubleDatabaseField(self, "TAX_CREDITS")
		val monthlyTotal = new NullableDoubleDatabaseField(self, "MONTHLY_TOTAL")
		val annualTotal = new NullableDoubleDatabaseField(self, "ANNUAL_TOTAL")
		val hourlyWage = new NullableDoubleDatabaseField(self, "HOURLY_WAGE")
		val precautionary = new NullableDoubleDatabaseField(self, "PRECAUTIONARY")
		val retirement = new NullableDoubleDatabaseField(self, "RETIREMENT")
		val childrensEducationTraining = new NullableDoubleDatabaseField(self, "CHILDRENS_EDUCATION_TRAINING")
		val homeownership = new NullableDoubleDatabaseField(self, "HOMEOWNERSHIP")
		val benefits = new NullableStringDatabaseField(self, "BENEFITS", 30)
		val monthlyTotalTemp = new NullableStringDatabaseField(self, "MONTHLY_TOTAL_TEMP", 50)
		val annualTotalTemp = new NullableStringDatabaseField(self, "ANNUAL_TOTAL_TEMP", 50)
		val workerCt = new NullableDoubleDatabaseField(self, "WORKER_CT")
		val infantCt = new NullableDoubleDatabaseField(self, "INFANT_CT")
		val preschoolerCt = new NullableDoubleDatabaseField(self, "PRESCHOOLER_CT")
		val schoolageCt = new NullableDoubleDatabaseField(self, "SCHOOLAGE_CT")
		val teenageCt = new NullableDoubleDatabaseField(self, "TEENAGE_CT")
	}

	def primaryKey: IntDatabaseField = fields.id
}