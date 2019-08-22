package Storable.StorableQuery

import Storable.Fields.DatabaseField
import Storable.Filter

case class ColumnAlias[T <: DatabaseField[_]](table: TableAlias, field: T ) {
	def filter(makeFilter: T => Filter): AliasedFilter = AliasedFilter(table.name, makeFilter(field))
}

object ColumnAlias {
	def wrap[T <: DatabaseField[_]](field: T): ColumnAlias[T] = ColumnAlias(TableAlias("t", field.entity), field)
}