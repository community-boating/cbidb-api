package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Fields.{DatabaseField, IntDatabaseField}
import org.sailcbi.APIServer.Storable.{FieldsObject, Filter, StorableClass, StorableObject}

case class ColumnAlias[T, U <: DatabaseField[T]](table: TableAlias, field: U) {
	def wrapFilter(f: U => String => Filter): Filter = f(field)(table.name)
}

object ColumnAlias {
	// Only use when you don't care about recovering e.g. IntDatabaseField from this field
	def wrap[T](field: DatabaseField[T]): ColumnAlias[T, _] = ColumnAlias(TableAlias.wrap(field.entity), field)
}