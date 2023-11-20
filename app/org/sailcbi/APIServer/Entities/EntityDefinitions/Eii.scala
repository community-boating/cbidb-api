package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Eii extends StorableClass(Eii) {
	override object values extends ValuesObject {
		val id = new IntFieldValue(self, Eii.fields.id)
		val desc7 = new NullableStringFieldValue(self, Eii.fields.desc7)
		val desc6 = new NullableStringFieldValue(self, Eii.fields.desc6)
		val desc5 = new NullableStringFieldValue(self, Eii.fields.desc5)
		val desc4 = new NullableStringFieldValue(self, Eii.fields.desc4)
		val desc3 = new NullableStringFieldValue(self, Eii.fields.desc3)
		val desc2 = new NullableStringFieldValue(self, Eii.fields.desc2)
		val desc1 = new StringFieldValue(self, Eii.fields.desc1)
		val housing = new DoubleFieldValue(self, Eii.fields.housing)
		val utilities = new DoubleFieldValue(self, Eii.fields.utilities)
		val food = new DoubleFieldValue(self, Eii.fields.food)
		val transportation = new DoubleFieldValue(self, Eii.fields.transportation)
		val childCare = new DoubleFieldValue(self, Eii.fields.childCare)
		val personalHouseholdNeeds = new DoubleFieldValue(self, Eii.fields.personalHouseholdNeeds)
		val healthcare = new DoubleFieldValue(self, Eii.fields.healthcare)
		val taxes = new DoubleFieldValue(self, Eii.fields.taxes)
		val taxCredits = new DoubleFieldValue(self, Eii.fields.taxCredits)
		val monthlyTotal = new DoubleFieldValue(self, Eii.fields.monthlyTotal)
		val annualTotal = new DoubleFieldValue(self, Eii.fields.annualTotal)
		val hourlyWage = new DoubleFieldValue(self, Eii.fields.hourlyWage)
		val precautionary = new DoubleFieldValue(self, Eii.fields.precautionary)
		val retirement = new DoubleFieldValue(self, Eii.fields.retirement)
		val childrensEducationTraining = new DoubleFieldValue(self, Eii.fields.childrensEducationTraining)
		val homeownership = new DoubleFieldValue(self, Eii.fields.homeownership)
		val benefits = new StringFieldValue(self, Eii.fields.benefits)
		val monthlyTotalTemp = new StringFieldValue(self, Eii.fields.monthlyTotalTemp)
		val annualTotalTemp = new StringFieldValue(self, Eii.fields.annualTotalTemp)
		val workerCt = new DoubleFieldValue(self, Eii.fields.workerCt)
		val infantCt = new DoubleFieldValue(self, Eii.fields.infantCt)
		val preschoolerCt = new DoubleFieldValue(self, Eii.fields.preschoolerCt)
		val schoolageCt = new DoubleFieldValue(self, Eii.fields.schoolageCt)
		val teenageCt = new DoubleFieldValue(self, Eii.fields.teenageCt)
	}
}

object Eii extends StorableObject[Eii] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EII"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		val desc7 = new NullableStringDatabaseField(self, "DESC_7", 30)
		val desc6 = new NullableStringDatabaseField(self, "DESC_6", 30)
		val desc5 = new NullableStringDatabaseField(self, "DESC_5", 30)
		val desc4 = new NullableStringDatabaseField(self, "DESC_4", 30)
		val desc3 = new NullableStringDatabaseField(self, "DESC_3", 30)
		val desc2 = new NullableStringDatabaseField(self, "DESC_2", 30)
		val desc1 = new StringDatabaseField(self, "DESC_1", 30)
		val housing = new DoubleDatabaseField(self, "HOUSING")
		val utilities = new DoubleDatabaseField(self, "UTILITIES")
		val food = new DoubleDatabaseField(self, "FOOD")
		val transportation = new DoubleDatabaseField(self, "TRANSPORTATION")
		val childCare = new DoubleDatabaseField(self, "CHILD_CARE")
		val personalHouseholdNeeds = new DoubleDatabaseField(self, "PERSONAL_HOUSEHOLD_NEEDS")
		val healthcare = new DoubleDatabaseField(self, "HEALTHCARE")
		val taxes = new DoubleDatabaseField(self, "TAXES")
		val taxCredits = new DoubleDatabaseField(self, "TAX_CREDITS")
		val monthlyTotal = new DoubleDatabaseField(self, "MONTHLY_TOTAL")
		val annualTotal = new DoubleDatabaseField(self, "ANNUAL_TOTAL")
		val hourlyWage = new DoubleDatabaseField(self, "HOURLY_WAGE")
		val precautionary = new DoubleDatabaseField(self, "PRECAUTIONARY")
		val retirement = new DoubleDatabaseField(self, "RETIREMENT")
		val childrensEducationTraining = new DoubleDatabaseField(self, "CHILDRENS_EDUCATION_TRAINING")
		val homeownership = new DoubleDatabaseField(self, "HOMEOWNERSHIP")
		val benefits = new StringDatabaseField(self, "BENEFITS", 30)
		val monthlyTotalTemp = new StringDatabaseField(self, "MONTHLY_TOTAL_TEMP", 50)
		val annualTotalTemp = new StringDatabaseField(self, "ANNUAL_TOTAL_TEMP", 50)
		val workerCt = new DoubleDatabaseField(self, "WORKER_CT")
		val infantCt = new DoubleDatabaseField(self, "INFANT_CT")
		val preschoolerCt = new DoubleDatabaseField(self, "PRESCHOOLER_CT")
		val schoolageCt = new DoubleDatabaseField(self, "SCHOOLAGE_CT")
		val teenageCt = new DoubleDatabaseField(self, "TEENAGE_CT")
	}

	def primaryKey: IntDatabaseField = fields.id
}