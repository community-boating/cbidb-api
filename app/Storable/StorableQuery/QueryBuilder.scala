package Storable.StorableQuery

case class QueryBuilder(fields: List[ColumnAlias[_]], joinPoints: List[JoinPoint[_]]) {
	// TODO: try to detect invalid setup and throw. e.g. two TableAliases, same name, different StorableObject
	// TODO: enforce n-1 join points for n tables unless a crossJoin flag is set
}