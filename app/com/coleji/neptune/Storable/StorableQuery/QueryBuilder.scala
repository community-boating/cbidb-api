package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

import scala.reflect.runtime.universe._

class QueryBuilder(
	val tables: List[TableAlias[_ <: StorableObject[_ <: StorableClass]]],
	val joins: List[TableJoin],
	val where: List[Filter],
	val fields: List[ColumnAlias[_]]
) {
	// TODO: try to detect invalid setup and throw. e.g. two TableAliases, same name, different StorableObject
	// TODO: enforce n-1 join points for n tables unless a crossJoin flag is set
	val tableAliasesHash: Set[String] = {
		this.tables.map(_.name).toSet
	}

	def innerJoin(t: TableAliasInnerJoined[_ <: StorableObject[_ <: StorableClass]], on: Filter): QueryBuilder = this.join(t, on)

	def outerJoin(t: TableAliasOuterJoined[_ <: StorableObject[_ <: StorableClass]], on: Filter): QueryBuilder = this.join(t, on)

	private def join(t: TableAlias[_ <: StorableObject[_ <: StorableClass]], on: Filter): QueryBuilder = new QueryBuilder(
		tables = t :: tables,
		joins = TableJoin(on) :: joins,
		where = where,
		fields = fields
	)

	def where(f: Filter): QueryBuilder = f match {
		case Filter.empty => this
		case ff => new QueryBuilder(
			tables = tables,
			joins = joins,
			where = List(ff),
			fields = fields
		)
	}

	def where(fs: List[Filter]): QueryBuilder = new QueryBuilder(
		tables = tables,
		joins = joins,
		where = fs.filter(f => f != Filter.empty),
		fields = fields
	)

	def select(fs: List[ColumnAlias[_]]): QueryBuilder = new QueryBuilder(
		tables = tables,
		joins = joins,
		where = where,
		fields = fs
	)
}

object QueryBuilder {
//	def from(o: StorableObject[_ <: StorableClass]): QueryBuilder = from(TableAlias.wrapForInnerJoin(o))

	def from(t: TableAlias[_ <: StorableObject[_ <: StorableClass]]): QueryBuilder = new QueryBuilder(
		tables = List(t),
		joins = List.empty,
		where = List.empty,
		fields = List.empty
	)

//	def allFieldsFromTable(ta: TableAlias[_ <: StorableObject[_ <: StorableClass]]): List[ColumnAlias[_]] = ta match {
//		case ti: TableAliasInnerJoined[_] => ti.obj.fieldList.map(ColumnAlias.wrapForInnerJoin)
//		case to: TableAliasOuterJoined[_] => to.obj.fieldList.map(ColumnAlias.wrapForOuterJoin)
//	}

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
