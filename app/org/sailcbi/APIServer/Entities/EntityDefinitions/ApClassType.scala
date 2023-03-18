package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassType extends StorableClass(ApClassType) {
	override object references extends ReferencesObject {
		val apClassFormats = new Initializable[IndexedSeq[ApClassFormat]]
	}

	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, ApClassType.fields.typeId)
		val typeName = new StringFieldValue(self, ApClassType.fields.typeName)
		val ratingPrereq = new NullableIntFieldValue(self,  ApClassType.fields.ratingPrereq)
		val classPrereq = new NullableIntFieldValue(self,  ApClassType.fields.classPrereq)
		val ratingOverkill = new NullableIntFieldValue(self,  ApClassType.fields.ratingOverkill)
		val displayOrder = new IntFieldValue(self, ApClassType.fields.displayOrder)
		val descShort = new NullableStringFieldValue(self,  ApClassType.fields.descShort)
		val descLong = new NullableStringFieldValue(self,  ApClassType.fields.descLong)
		val classOverkill = new NullableIntFieldValue(self,  ApClassType.fields.classOverkill)
		val noSignup = new BooleanFieldValue(self,  ApClassType.fields.noSignup)
		val priceDefault = new NullableDoubleFieldValue(self, ApClassType.fields.priceDefault)
		val signupMinDefault = new NullableIntFieldValue(self,  ApClassType.fields.signupMinDefault)
		val signupMaxDefault = new NullableIntFieldValue(self,  ApClassType.fields.signupMaxDefault)
		val disallowIfOverkill = new BooleanFieldValue(self,  ApClassType.fields.disallowIfOverkill)
	}
}

object ApClassType extends StorableObject[ApClassType] {
	val entityName: String = "AP_CLASS_TYPES"

	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 200)
		val ratingPrereq = new NullableIntDatabaseField(self, "RATING_PREREQ")
		val classPrereq = new NullableIntDatabaseField(self, "CLASS_PREREQ")
		val ratingOverkill = new NullableIntDatabaseField(self, "RATING_OVERKILL")
		val displayOrder = new IntDatabaseField(self, "DISPLAY_ORDER")
		val descShort = new NullableStringDatabaseField(self, "DESC_SHORT", 4000)
		val descLong = new NullableStringDatabaseField(self, "DESC_LONG", 4000)
		val classOverkill = new NullableIntDatabaseField(self, "CLASS_OVERKILL")
		val noSignup = new BooleanDatabaseField(self, "NO_SIGNUP", true)
		val priceDefault = new NullableDoubleDatabaseField(self, "PRICE_DEFAULT")
		val signupMinDefault = new NullableIntDatabaseField(self, "SIGNUP_MIN_DEFAULT")
		val signupMaxDefault = new NullableIntDatabaseField(self, "SIGNUP_MAX_DEFAULT")
		val disallowIfOverkill = new BooleanDatabaseField(self, "DISALLOW_IF_OVERKILL", true)
	}

	def primaryKey: IntDatabaseField = fields.typeId
}
