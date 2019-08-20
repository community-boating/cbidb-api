package Storable.StorableQuery

case class JoinPoint[T](a: ColumnAlias[T], b: ColumnAlias[T])
