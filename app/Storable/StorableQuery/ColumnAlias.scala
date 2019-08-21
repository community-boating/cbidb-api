package Storable.StorableQuery

import Storable.Fields.DatabaseField

case class ColumnAlias[T](table: TableAlias, field: DatabaseField[T])

object ColumnAlias {
	def wrap(field: DatabaseField[_]): ColumnAlias[_] = ColumnAlias(TableAlias("t", field.entity), field)
}