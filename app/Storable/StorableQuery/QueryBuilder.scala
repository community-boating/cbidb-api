package Storable.StorableQuery

import Storable.Fields.DatabaseField

case class QueryBuilder(fields: List[ColumnAlias[_ <: DatabaseField[_]]], joinPoints: List[JoinPoint[_ <: DatabaseField[_]]], filters: List[AliasedFilter]) {
	// TODO: try to detect invalid setup and throw. e.g. two TableAliases, same name, different StorableObject
	// TODO: enforce n-1 join points for n tables unless a crossJoin flag is set
}