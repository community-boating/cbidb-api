package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApDockRptRating extends StorableClass(ApDockRptRating) {
	object values extends ValuesObject {
		val drRatingId = new IntFieldValue(self, ApDockRptRating.fields.drRatingId)
		val drId = new NullableIntFieldValue(self, ApDockRptRating.fields.drId)
		val ratingId = new NullableIntFieldValue(self, ApDockRptRating.fields.ratingId)
		val ratingCt = new NullableDoubleFieldValue(self, ApDockRptRating.fields.ratingCt)
		val numberTested = new NullableDoubleFieldValue(self, ApDockRptRating.fields.numberTested)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApDockRptRating.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApDockRptRating.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApDockRptRating.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApDockRptRating.fields.updatedBy)
	}
}

object ApDockRptRating extends StorableObject[ApDockRptRating] {
	val entityName: String = "AP_DOCK_RPT_RATINGS"

	object fields extends FieldsObject {
		val drRatingId = new IntDatabaseField(self, "DR_RATING_ID")
		val drId = new NullableIntDatabaseField(self, "DR_ID")
		val ratingId = new NullableIntDatabaseField(self, "RATING_ID")
		val ratingCt = new NullableDoubleDatabaseField(self, "RATING_CT")
		val numberTested = new NullableDoubleDatabaseField(self, "NUMBER_TESTED")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.drRatingId
}