package Storable.Fields.FieldValue

import Storable.Fields.NullableDoubleDatabaseField
import Storable.StorableClass

class NullableDoubleFieldValue(instance: StorableClass, field: NullableDoubleDatabaseField) extends FieldValue[Option[Double]](instance, field) {
	def getPersistenceLiteral: String = super.get match {
		case Some(x) => x.toString
		case None => "NULL"
	}
}