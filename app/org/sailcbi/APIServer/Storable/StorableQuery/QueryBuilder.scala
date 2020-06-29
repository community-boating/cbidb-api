package org.sailcbi.APIServer.Storable.StorableQuery

import org.sailcbi.APIServer.Storable.Filter
import scala.reflect.runtime.universe._

class QueryBuilder(
	val tables: List[TableAlias],
	val joins: List[TableJoin],
	val where: List[Filter],
	val fields: List[ColumnAlias[_, _]]
) {
	// TODO: try to detect invalid setup and throw. e.g. two TableAliases, same name, different StorableObject
	// TODO: enforce n-1 join points for n tables unless a crossJoin flag is set
	val tableAliasesHash: Set[String] = {
		this.tables.map(_.name).toSet
	}

	def innerJoin(t: TableAlias, on: Filter): QueryBuilder = this.join(INNER_JOIN)(t, on)

	def leftOuterJoin(t: TableAlias, on: Filter): QueryBuilder = this.join(LEFT_OUTER_JOIN)(t, on)

	private def join(joinType: JoinType)(t: TableAlias, on: Filter): QueryBuilder = new QueryBuilder(
		tables = t :: tables,
		joins = TableJoin(joinType, on) :: joins,
		where = where,
		fields = fields
	)

	def where(f: Filter): QueryBuilder = new QueryBuilder(
		tables = tables,
		joins = joins,
		where = List(f),
		fields = fields
	)

	def where(fs: List[Filter]): QueryBuilder = new QueryBuilder(
		tables = tables,
		joins = joins,
		where = fs,
		fields = fields
	)

	def select(fs: List[ColumnAlias[_, _]]): QueryBuilder = new QueryBuilder(
		tables = tables,
		joins = joins,
		where = where,
		fields = fs
	)
}

object QueryBuilder {
	def from(t: TableAlias): QueryBuilder = new QueryBuilder(
		tables = List(t),
		joins = List.empty,
		where = List.empty,
		fields = List.empty
	)

	// Using reflection, scan through all val members of a given object
	// Return all the ones that are ColumnAlias[_, _] in a list
	def colsAsList(obj: AnyRef): List[ColumnAlias[_, _]] = {
		val rm = scala.reflect.runtime.currentMirror
		val values = rm.classSymbol(obj.getClass).toType.members.collect {
			case m: TermSymbol if m.isVal => m
		}
		val instanceMirror = rm.reflect(obj)
		var ret: List[ColumnAlias[_, _]] = List.empty
		for (value <- values) {
			instanceMirror.reflectField(value).get match {
				case ca: ColumnAlias[_, _] => {
					ret = ca :: ret
				}
				case _ =>
			}
		}
		ret
	}
}