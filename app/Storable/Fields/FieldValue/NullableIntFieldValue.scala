package Storable.Fields.FieldValue

import Storable.Fields.NullableIntDatabaseField
import Storable.StorableClass

class NullableIntFieldValue(instance: StorableClass, field: NullableIntDatabaseField) extends FieldValue[Option[Int]](instance, field) {
	def getPersistenceLiteral: String = super.get match {
		case Some(x) => x.toString
		case None => "NULL"
	}
}