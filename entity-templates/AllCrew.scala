package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class AllCrew extends StorableClass(AllCrew) {
	object values extends ValuesObject {
		val pk = new NullableDoubleFieldValue(self, AllCrew.fields.pk)
		val nameFirst = new NullableStringFieldValue(self, AllCrew.fields.nameFirst)
		val nameLast = new NullableStringFieldValue(self, AllCrew.fields.nameLast)
		val cardNum = new NullableStringFieldValue(self, AllCrew.fields.cardNum)
		val signoutId = new NullableIntFieldValue(self, AllCrew.fields.signoutId)
		val cI = new NullableBooleanFIeldValue(self, AllCrew.fields.cI)
		val startActive = new NullableLocalDateTimeFieldValue(self, AllCrew.fields.startActive)
		val endActive = new NullableLocalDateTimeFieldValue(self, AllCrew.fields.endActive)
		val personId = new NullableIntFieldValue(self, AllCrew.fields.personId)
	}
}

object AllCrew extends StorableObject[AllCrew] {
	val entityName: String = "ALL_CREW"

	object fields extends FieldsObject {
		val pk = new NullableDoubleDatabaseField(self, "PK")
		val nameFirst = new NullableStringDatabaseField(self, "NAME_FIRST", 100)
		val nameLast = new NullableStringDatabaseField(self, "NAME_LAST", 100)
		val cardNum = new NullableStringDatabaseField(self, "CARD_NUM", 50)
		val signoutId = new NullableIntDatabaseField(self, "SIGNOUT_ID")
		val cI = new NullableBooleanDatabaseField(self, "C_I")
		val startActive = new NullableLocalDateTimeDatabaseField(self, "START_ACTIVE")
		val endActive = new NullableLocalDateTimeDatabaseField(self, "END_ACTIVE")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
	}
}