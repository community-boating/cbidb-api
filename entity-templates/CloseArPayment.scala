package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CloseArPayment extends StorableClass(CloseArPayment) {
	object values extends ValuesObject {
		val closeId = new NullableIntFieldValue(self, CloseArPayment.fields.closeId)
		val amount = new NullableDoubleFieldValue(self, CloseArPayment.fields.amount)
	}
}

object CloseArPayment extends StorableObject[CloseArPayment] {
	val entityName: String = "CLOSE_AR_PAYMENTS"

	object fields extends FieldsObject {
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
	}
}