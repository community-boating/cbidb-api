package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MostRecentSignout extends StorableClass(MostRecentSignout) {
	object values extends ValuesObject {
		val boatId = new IntFieldValue(self, MostRecentSignout.fields.boatId)
		val sailNumber = new NullableStringFieldValue(self, MostRecentSignout.fields.sailNumber)
		val signoutId = new IntFieldValue(self, MostRecentSignout.fields.signoutId)
	}
}

object MostRecentSignout extends StorableObject[MostRecentSignout] {
	val entityName: String = "MOST_RECENT_SIGNOUTS"

	object fields extends FieldsObject {
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val sailNumber = new NullableStringDatabaseField(self, "SAIL_NUMBER", 15)
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
	}
}