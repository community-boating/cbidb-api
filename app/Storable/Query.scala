package Storable

import Storable.Fields.DatabaseField

class Query[T <: StorableClass](obj: StorableObject[T], fs: FieldsObject => List[DatabaseField[_]]) {
	lazy val doThing: List[DatabaseField[_]] = fs(obj.fields)
}

object Query {
	def apply[T <: StorableClass](obj: StorableObject[T])(fs: obj.fields.type => List[DatabaseField[_]]) = new Query[T](obj, fs.asInstanceOf[FieldsObject => List[DatabaseField[_]]])
}