package Storable.StorableQuery

import Storable.Fields.DatabaseField

case class JoinPoint[T <: DatabaseField[_]](a: ColumnAlias[T], b: ColumnAlias[T])
