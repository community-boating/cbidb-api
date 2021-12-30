package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.DatabaseField
import com.coleji.neptune.Storable.{StorableClass, StorableObject}

sealed abstract class TableAlias[T <: StorableObject[_ <: StorableClass]](val obj: T, val name: String) {
	val r = "^[a-zA-Z_]+$".r
	if (r.findFirstMatchIn(name).isEmpty) throw new Exception("Invalid table alias name")

	type Fields = obj.fields.type
	private val fields: Fields = obj.fields
	def wrappedFields[U <: DatabaseField[_]](getField: Fields => U): ColumnAlias[U] = {
		val field = getField(obj.fields.asInstanceOf[Fields])
		field.abstractAlias(this).asInstanceOf[ColumnAlias[U]]
	}
	def wrappedFields(getFields: Fields => List[DatabaseField[_]]): List[ColumnAlias[_]] = {
		val fields = getFields(obj.fields.asInstanceOf[Fields])
		fields.map(_.abstractAlias(this))
	}

	override def toString: String = s"(${obj.entityName} as ${name})"
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
	implicit def apply[T <: StorableObject[_ <: StorableClass]](obj: T): TableAliasInnerJoined[T] = wrapForInnerJoin(obj)
}
