package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ClosePkgView extends StorableClass(ClosePkgView) {
	object values extends ValuesObject {
		val closeId = new IntFieldValue(self, ClosePkgView.fields.closeId)
		val getCashTotal = new NullableDoubleFieldValue(self, ClosePkgView.fields.getCashTotal)
		val getInpersonArTotal = new NullableDoubleFieldValue(self, ClosePkgView.fields.getInpersonArTotal)
		val getCheckTotal = new NullableDoubleFieldValue(self, ClosePkgView.fields.getCheckTotal)
		val getInpersonTally = new NullableDoubleFieldValue(self, ClosePkgView.fields.getInpersonTally)
		val getOnlineCcTotal = new NullableDoubleFieldValue(self, ClosePkgView.fields.getOnlineCcTotal)
		val getOnlineAr = new NullableDoubleFieldValue(self, ClosePkgView.fields.getOnlineAr)
		val getOnlineTally = new NullableDoubleFieldValue(self, ClosePkgView.fields.getOnlineTally)
	}
}

object ClosePkgView extends StorableObject[ClosePkgView] {
	val entityName: String = "CLOSE_PKG_VIEW"

	object fields extends FieldsObject {
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val getCashTotal = new NullableDoubleDatabaseField(self, "GET_CASH_TOTAL")
		val getInpersonArTotal = new NullableDoubleDatabaseField(self, "GET_INPERSON_AR_TOTAL")
		val getCheckTotal = new NullableDoubleDatabaseField(self, "GET_CHECK_TOTAL")
		val getInpersonTally = new NullableDoubleDatabaseField(self, "GET_INPERSON_TALLY")
		val getOnlineCcTotal = new NullableDoubleDatabaseField(self, "GET_ONLINE_CC_TOTAL")
		val getOnlineAr = new NullableDoubleDatabaseField(self, "GET_ONLINE_AR")
		val getOnlineTally = new NullableDoubleDatabaseField(self, "GET_ONLINE_TALLY")
	}
}