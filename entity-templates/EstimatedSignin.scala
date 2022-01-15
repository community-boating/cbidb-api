package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EstimatedSignin extends StorableClass(EstimatedSignin) {
	object values extends ValuesObject {
		val signoutId = new IntFieldValue(self, EstimatedSignin.fields.signoutId)
		val estSigninDatetime = new NullableLocalDateTimeFieldValue(self, EstimatedSignin.fields.estSigninDatetime)
	}
}

object EstimatedSignin extends StorableObject[EstimatedSignin] {
	val entityName: String = "ESTIMATED_SIGNINS"

	object fields extends FieldsObject {
		val signoutId = new IntDatabaseField(self, "SIGNOUT_ID")
		val estSigninDatetime = new NullableLocalDateTimeDatabaseField(self, "EST_SIGNIN_DATETIME")
	}
}