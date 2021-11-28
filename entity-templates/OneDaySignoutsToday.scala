package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OneDaySignoutsToday extends StorableClass(OneDaySignoutsToday) {
	object values extends ValuesObject {
		val signoutId = new IntFieldValue(self, OneDaySignoutsToday.fields.signoutId)
	}
}

object OneDaySignoutsToday extends StorableObject[OneDaySignoutsToday] {
	val entityName: String = "ONE_DAY_SIGNOUTS_TODAY"

	object fields extends FieldsObject {
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
	}
}