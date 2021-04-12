package com.coleji.framework.Storable.StorableQuery

import com.coleji.framework.Storable.{Filter, StorableClass, StorableObject}

import scala.reflect.runtime.universe._

class QueryBuilder(
	val tables: List[(TableAlias[_ <: StorableObject[_ <: StorableClass]], Boolean)],
	val joins: List[TableJoin],
	val where: List[Filter],
	val fields: List[ColumnAlias[_]]
) {
	// TODO: try to detect invalid setup and throw. e.g. two TableAliases, same name, different StorableObject
	// TODO: enforce n-1 join points for n tables unless a crossJoin flag is set
	val tableAliasesHash: Set[String] = {
		this.tables.map(_._1.name).toSet
	}

	def innerJoin(t: TableAliasInnerJoined[_ <: StorableObject[_ <: StorableClass]], on: Filter): QueryBuilder = this.join(t, on, false)

	def outerJoin(t: TableAliasOuterJoined[_ <: StorableObject[_ <: StorableClass]], on: Filter): QueryBuilder = this.join(t, on, true)

	private def join(t: TableAlias[_ <: StorableObject[_ <: StorableClass]], on: Filter, outerJoin: Boolean): QueryBuilder = new QueryBuilder(
		tables = (t, outerJoin) :: tables,
		joins = TableJoin(on) :: joins,
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

	def select(fs: List[ColumnAlias[_]]): QueryBuilder = new QueryBuilder(
		tables = tables,
		joins = joins,
		where = where,
		fields = fs
	)

	def allFields: List[ColumnAlias[_]] = {
		tables.flatMap(t => {
			val (tableAlias, outerJoin) = t
			if (outerJoin) {
				tableAlias.obj.fieldList.map(ColumnAlias.wrapForOuterJoin)
			} else {
				tableAlias.obj.fieldList.map(ColumnAlias.wrapForInnerJoin)
			}
		})
	}
}

object QueryBuilder {
	def from(t: TableAlias[_ <: StorableObject[_ <: StorableClass]]): QueryBuilder = new QueryBuilder(
		tables = List((t, false)),
		joins = List.empty,
		where = List.empty,
		fields = List.empty
	)

	// Using reflection, scan through all val members of a given object
	// Return all the ones that are ColumnAlias[_] in a list
	def colsAsList(obj: AnyRef): List[ColumnAlias[_]] = {
		val rm = scala.reflect.runtime.currentMirror
		val values = rm.classSymbol(obj.getClass).toType.members.collect {
			case m: TermSymbol if m.isVal => m
		}
		val instanceMirror = rm.reflect(obj)
		var ret: List[ColumnAlias[_]] = List.empty
		for (value <- values) {
			instanceMirror.reflectField(value).get match {
				case ca: ColumnAlias[_] => {
					ret = ca :: ret
				}
				case _ =>
			}
		}
		ret
	}
}
