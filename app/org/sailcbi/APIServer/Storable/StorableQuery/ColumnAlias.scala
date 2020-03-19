package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.{FieldsObject, Filter, StorableClass, StorableObject}

case class ColumnAlias[T](table: TableAlias, field: DatabaseField[T]) {
	def filter(makeFilter: DatabaseField[T] => Filter): Filter = makeFilter(field)
}

object ColumnAlias {
	def wrap[T](field: DatabaseField[T]): ColumnAlias[T] = ColumnAlias(TableAlias.wrap(field.entity), field)
}