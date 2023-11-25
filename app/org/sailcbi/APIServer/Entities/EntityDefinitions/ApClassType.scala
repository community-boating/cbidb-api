package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class ApClassType extends StorableClass(ApClassType) {
	override object references extends ReferencesObject {
		val apClassFormats = new InitializableSeq[ApClassFormat, IndexedSeq[ApClassFormat]]
	}

	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, ApClassType.fields.typeId)
		val typeName = new StringFieldValue(self, ApClassType.fields.typeName)
		val ratingPrereq = new NullableIntFieldValue(self, ApClassType.fields.ratingPrereq)
		val classPrereq = new NullableIntFieldValue(self, ApClassType.fields.classPrereq)
		val ratingOverkill = new NullableIntFieldValue(self, ApClassType.fields.ratingOverkill)
		val createdOn = new DateTimeFieldValue(self, ApClassType.fields.createdOn)
		val createdBy = new StringFieldValue(self, ApClassType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ApClassType.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, ApClassType.fields.updatedBy)
		val displayOrder = new DoubleFieldValue(self, ApClassType.fields.displayOrder)
		val descLong = new StringFieldValue(self, ApClassType.fields.descLong)
		val descShort = new NullableStringFieldValue(self, ApClassType.fields.descShort)
		val classOverkill = new NullableIntFieldValue(self, ApClassType.fields.classOverkill)
		val colorCode = new NullableStringFieldValue(self, ApClassType.fields.colorCode)
		val noSignup = new BooleanFieldValue(self, ApClassType.fields.noSignup)
		val priceDefault = new NullableDoubleFieldValue(self, ApClassType.fields.priceDefault)
		val signupMaxDefault = new NullableIntFieldValue(self, ApClassType.fields.signupMaxDefault)
		val signupMinDefault = new NullableIntFieldValue(self, ApClassType.fields.signupMinDefault)
		val disallowIfOverkill = new BooleanFieldValue(self, ApClassType.fields.disallowIfOverkill)
		val teachUrl = new NullableStringFieldValue(self, ApClassType.fields.teachUrl)
	}
}

object ApClassType extends StorableObject[ApClassType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 200)
		val ratingPrereq = new NullableIntDatabaseField(self, "RATING_PREREQ")
		val classPrereq = new NullableIntDatabaseField(self, "CLASS_PREREQ")
		val ratingOverkill = new NullableIntDatabaseField(self, "RATING_OVERKILL")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		@NullableInDatabase
		val descLong = new StringDatabaseField(self, "DESC_LONG", -1)
		val descShort = new NullableStringDatabaseField(self, "DESC_SHORT", -1)
		val classOverkill = new NullableIntDatabaseField(self, "CLASS_OVERKILL")
		val colorCode = new NullableStringDatabaseField(self, "COLOR_CODE", 6)
		@NullableInDatabase
		val noSignup = new BooleanDatabaseField(self, "NO_SIGNUP", true)
		val priceDefault = new NullableDoubleDatabaseField(self, "PRICE_DEFAULT")
		val signupMaxDefault = new NullableIntDatabaseField(self, "SIGNUP_MAX_DEFAULT")
		val signupMinDefault = new NullableIntDatabaseField(self, "SIGNUP_MIN_DEFAULT")
		@NullableInDatabase
		val disallowIfOverkill = new BooleanDatabaseField(self, "DISALLOW_IF_OVERKILL", true)
		val teachUrl = new NullableStringDatabaseField(self, "TEACH_URL", 100)
	}

	def primaryKey: IntDatabaseField = fields.typeId
}