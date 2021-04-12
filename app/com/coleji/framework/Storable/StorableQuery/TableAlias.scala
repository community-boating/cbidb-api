package com.coleji.framework.Storable.StorableQuery

import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.{StorableClass, StorableObject}

sealed abstract class TableAlias[T <: StorableObject[_ <: StorableClass]](val obj: T, val name: String) {
	// TODO: throw if name has invalid characters.  for safety i think only [a-zA-Z]
}

case class TableAliasInnerJoined[T <: StorableObject[_ <: StorableClass]](override val obj: T, override val name: String) extends TableAlias[T](obj, name) {
	def wrappedFields[U <: DatabaseField[_]](getField: T => U): ColumnAliasInnerJoined[U] = {
		val f = getField(obj)
		val ret = f.alias(this).asInstanceOf[ColumnAliasInnerJoined[U]]
		ret
	}
	def construct(qbrr: QueryBuilderResultRow): obj.InstanceType =
		obj.construct(qbrr, this.asInstanceOf[TableAliasInnerJoined[obj.type]]).asInstanceOf[obj.InstanceType]
}
case class TableAliasOuterJoined[T <: StorableObject[_ <: StorableClass]](override val obj: T, override val name: String) extends TableAlias[T](obj, name) {
	def wrappedFields[U <: DatabaseField[_]](getField: T => U): ColumnAliasOuterJoined[U] = {
		val f: U = getField(obj)
		val ret = f.alias(this).asInstanceOf[ColumnAliasOuterJoined[U]]
		ret
	}
	def construct(qbrr: QueryBuilderResultRow): Option[obj.InstanceType] =
		obj.construct(qbrr, this.asInstanceOf[TableAliasOuterJoined[obj.type]]).asInstanceOf[Option[obj.InstanceType]]
}

object TableAlias {
	def wrapForInnerJoin[T <: StorableObject[_ <: StorableClass]](obj: T): TableAliasInnerJoined[T] = TableAliasInnerJoined(obj, obj.entityName)
	def wrapForOuterJoin[T <: StorableObject[_ <: StorableClass]](obj: T): TableAliasOuterJoined[T] = TableAliasOuterJoined(obj, obj.entityName)
	def apply[T <: StorableObject[_ <: StorableClass]](obj: T): TableAliasInnerJoined[T] = wrapForInnerJoin(obj)
}
