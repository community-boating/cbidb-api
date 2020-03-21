package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.Filter

case class QueryBuilder(
	fields: List[ColumnAlias[_, _]],
	joinPoints: List[JoinPoint[_]],
	filters: List[Filter]
) {
	// TODO: try to detect invalid setup and throw. e.g. two TableAliases, same name, different StorableObject
	// TODO: enforce n-1 join points for n tables unless a crossJoin flag is set
}