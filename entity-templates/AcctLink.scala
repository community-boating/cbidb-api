package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class AcctLink extends StorableClass(AcctLink) {
	object values extends ValuesObject {
		val a = new NullableDoubleFieldValue(self, AcctLink.fields.a)
		val b = new NullableDoubleFieldValue(self, AcctLink.fields.b)
		val typeId = new NullableIntFieldValue(self, AcctLink.fields.typeId)
	}
}

object AcctLink extends StorableObject[AcctLink] {
	val entityName: String = "ACCT_LINKS"

	object fields extends FieldsObject {
		val a = new NullableDoubleDatabaseField(self, "A")
		val b = new NullableDoubleDatabaseField(self, "B")
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
	}
}