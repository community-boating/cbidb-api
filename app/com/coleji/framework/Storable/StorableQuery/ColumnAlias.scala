package com.coleji.framework.Storable.StorableQuery

import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.{Filter, StorableClass, StorableObject}

sealed abstract class ColumnAlias[U <: DatabaseField[_]](val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], val field: U) {
	def wrapFilter(f: U => String => Filter): Filter = f(field)(table.name)
}

case class ColumnAliasInnerJoined[U <: DatabaseField[_]](override val table: TableAliasInnerJoined[_ <: StorableObject[_ <: StorableClass]], override val field: U) extends ColumnAlias[U](table, field)
case class ColumnAliasOuterJoined[U <: DatabaseField[_]](override val table: TableAliasOuterJoined[_ <: StorableObject[_ <: StorableClass]], override val field: U) extends ColumnAlias[U](table, field)

object ColumnAlias {
	// Only use when you don't care about recovering e.g. IntDatabaseField from this field
	def wrapForInnerJoin[U <: DatabaseField[_]](field: U): ColumnAliasInnerJoined[U] = ColumnAliasInnerJoined(TableAlias.wrapForInnerJoin(field.entity), field)
	def wrapForOuterJoin[U <: DatabaseField[_]](field: U): ColumnAliasOuterJoined[U] = ColumnAliasOuterJoined(TableAlias.wrapForOuterJoin(field.entity), field)
}