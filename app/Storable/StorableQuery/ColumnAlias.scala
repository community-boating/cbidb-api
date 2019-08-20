package Storable.StorableQuery

import Storable.Fields.DatabaseField

case class ColumnAlias[T](table: TableAlias, field: DatabaseField[T])
