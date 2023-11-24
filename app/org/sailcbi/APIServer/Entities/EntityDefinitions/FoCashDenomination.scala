package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoCashDenomination extends StorableClass(FoCashDenomination) {
	override object values extends ValuesObject {
		val denomIndex = new IntFieldValue(self, FoCashDenomination.fields.denomIndex)
		val denomination = new DoubleFieldValue(self, FoCashDenomination.fields.denomination)
		val billCoin = new StringFieldValue(self, FoCashDenomination.fields.billCoin)
	}
}

object FoCashDenomination extends StorableObject[FoCashDenomination] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_CASH_DENOMINATIONS"

	object fields extends FieldsObject {
		val denomIndex = new IntDatabaseField(self, "DENOM_INDEX")
		val denomination = new DoubleDatabaseField(self, "DENOMINATION")
		val billCoin = new StringDatabaseField(self, "BILL_COIN", 1)
	}

	def primaryKey: IntDatabaseField = fields.denomIndex
}