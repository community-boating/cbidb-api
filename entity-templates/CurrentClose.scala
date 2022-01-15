package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CurrentClose extends StorableClass(CurrentClose) {
	object values extends ValuesObject {
		val inpersonClose = new DoubleFieldValue(self, CurrentClose.fields.inpersonClose)
		val onlineClose = new DoubleFieldValue(self, CurrentClose.fields.onlineClose)
	}
}

object CurrentClose extends StorableObject[CurrentClose] {
	val entityName: String = "CURRENT_CLOSES"

	object fields extends FieldsObject {
		val inpersonClose = new DoubleDatabaseField(self, "INPERSON_CLOSE")
		val onlineClose = new DoubleDatabaseField(self, "ONLINE_CLOSE")
	}
}