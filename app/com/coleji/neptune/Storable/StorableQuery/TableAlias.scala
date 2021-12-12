package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.DatabaseField
import com.coleji.neptune.Storable.{StorableClass, StorableObject}

sealed abstract class TableAlias[T <: StorableObject[_ <: StorableClass]](val obj: T, val name: String) {
	// TODO: throw if name has invalid characters.  for safety i think only [a-zA-Z]
	type Fields = obj.fields.type
	val fields = obj.fields
	def wrappedFields[U <: DatabaseField[_]](getField: Fields => U): ColumnAlias[U] = {
		val f = getField(obj.fields)
		val ret = f.alias(this)
		ret
	}
}

case class TableAliasInnerJoined[T <: StorableObject[_ <: StorableClass]](override val obj: T, override val name: String) extends TableAlias[T](obj, name) {
	def construct(qbrr: QueryBuilderResultRow): obj.InstanceType =
		obj.construct(qbrr, this.asInstanceOf[TableAliasInnerJoined[obj.type]]).asInstanceOf[obj.InstanceType]
}
case class TableAliasOuterJoined[T <: StorableObject[_ <: StorableClass]](override val obj: T, override val name: String) extends TableAlias[T](obj, name) {

	def construct(qbrr: QueryBuilderResultRow): Option[obj.InstanceType] =
		obj.construct(qbrr, this.asInstanceOf[TableAliasOuterJoined[obj.type]]).asInstanceOf[Option[obj.InstanceType]]
}

object TableAlias {
	def wrapForInnerJoin[T <: StorableObject[_ <: StorableClass]](obj: T): TableAliasInnerJoined[T] = TableAliasInnerJoined(obj, obj.entityName)
	def wrapForOuterJoin[T <: StorableObject[_ <: StorableClass]](obj: T): TableAliasOuterJoined[T] = TableAliasOuterJoined(obj, obj.entityName)
	def apply[T <: StorableObject[_ <: StorableClass]](obj: T): TableAliasInnerJoined[T] = wrapForInnerJoin(obj)
}
