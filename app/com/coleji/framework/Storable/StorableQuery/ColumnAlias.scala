package com.coleji.framework.Storable.StorableQuery

import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.Filter

sealed abstract class ColumnAlias[T, U <: DatabaseField[T]](val table: TableAlias, val field: U) {
	def wrapFilter(f: U => String => Filter): Filter = f(field)(table.name)
}

case class ColumnAliasInnerJoined[T, U <: DatabaseField[T]](override val table: TableAliasInnerJoined, override val field: U) extends ColumnAlias[T, U](table, field)
case class ColumnAliasOuterJoined[T, U <: DatabaseField[T]](override val table: TableAliasOuterJoined, override val field: U) extends ColumnAlias[T, U](table, field)

object ColumnAlias {
	// Only use when you don't care about recovering e.g. IntDatabaseField from this field
	def wrapForInnerJoin[T](field: DatabaseField[T]): ColumnAliasInnerJoined[T, _] = ColumnAliasInnerJoined(TableAlias.wrapForInnerJoin(field.entity), field)
	def wrapForOuterJoin[T](field: DatabaseField[T]): ColumnAliasOuterJoined[T, _] = ColumnAliasOuterJoined(TableAlias.wrapForOuterJoin(field.entity), field)
}