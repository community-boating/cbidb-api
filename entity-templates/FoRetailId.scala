package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoRetailId extends StorableClass(FoRetailId) {
	object values extends ValuesObject {
		val class = new NullableBooleanFIeldValue(self, FoRetailId.fields.class)
		val id = new NullableIntFieldValue(self, FoRetailId.fields.id)
		val taxable = new NullableBooleanFIeldValue(self, FoRetailId.fields.taxable)
	}
}

object FoRetailId extends StorableObject[FoRetailId] {
	val entityName: String = "FO_RETAIL_IDS"

	object fields extends FieldsObject {
		val class = new NullableBooleanDatabaseField(self, "CLASS")
		val id = new NullableIntDatabaseField(self, "ID")
		val taxable = new NullableBooleanDatabaseField(self, "TAXABLE")
	}
}