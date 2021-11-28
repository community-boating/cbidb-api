package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class UnitTestScratch extends StorableClass(UnitTestScratch) {
	object values extends ValuesObject {
		val pk = new DoubleFieldValue(self, UnitTestScratch.fields.pk)
		val colNumber = new NullableDoubleFieldValue(self, UnitTestScratch.fields.colNumber)
		val colVarchar10 = new NullableStringFieldValue(self, UnitTestScratch.fields.colVarchar10)
		val colDate = new NullableLocalDateTimeFieldValue(self, UnitTestScratch.fields.colDate)
		val colTimestamp = new NullableUnknownFieldType(self, UnitTestScratch.fields.colTimestamp)
		val colClob = new NullableUnknownFieldType(self, UnitTestScratch.fields.colClob)
		val colChar = new NullableBooleanFIeldValue(self, UnitTestScratch.fields.colChar)
	}
}

object UnitTestScratch extends StorableObject[UnitTestScratch] {
	val entityName: String = "UNIT_TEST_SCRATCH"

	object fields extends FieldsObject {
		val pk = new DoubleDatabaseField(self, "PK")
		val colNumber = new NullableDoubleDatabaseField(self, "COL_NUMBER")
		val colVarchar10 = new NullableStringDatabaseField(self, "COL_VARCHAR_10", 10)
		val colDate = new NullableLocalDateTimeDatabaseField(self, "COL_DATE")
		val colTimestamp = new NullableUnknownFieldType(self, "COL_TIMESTAMP")
		val colClob = new NullableUnknownFieldType(self, "COL_CLOB")
		val colChar = new NullableBooleanDatabaseField(self, "COL_CHAR")
	}

	def primaryKey: IntDatabaseField = fields.pk
}