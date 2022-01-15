package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoCashDenomination extends StorableClass(FoCashDenomination) {
	object values extends ValuesObject {
		val denomIndex = new DoubleFieldValue(self, FoCashDenomination.fields.denomIndex)
		val denomination = new DoubleFieldValue(self, FoCashDenomination.fields.denomination)
		val billCoin = new BooleanFIeldValue(self, FoCashDenomination.fields.billCoin)
	}
}

object FoCashDenomination extends StorableObject[FoCashDenomination] {
	val entityName: String = "FO_CASH_DENOMINATIONS"

	object fields extends FieldsObject {
		val denomIndex = new DoubleDatabaseField(self, "DENOM_INDEX")
		val denomination = new DoubleDatabaseField(self, "DENOMINATION")
		val billCoin = new BooleanDatabaseField(self, "BILL_COIN")
	}

	def primaryKey: IntDatabaseField = fields.denomIndex
}