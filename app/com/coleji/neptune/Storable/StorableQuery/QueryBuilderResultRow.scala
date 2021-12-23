package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, NonNullFieldWasNullException}
import com.coleji.neptune.Storable.{ProtoStorable, StorableObject}

class QueryBuilderResultRow(val ps: ProtoStorable) {
//	def getValue[T](field: ColumnAliasInnerJoined[_ <: DatabaseField[T]]): T =
//		field.field.findValueInProtoStorable(ps, field).get
//
//
//	def getValue[T](field: ColumnAliasOuterJoined[_ <: DatabaseField[T]]): Option[T] = {
//		try {
//			field.field.findValueInProtoStorable(ps, field)
//		} catch {
//			case e: NonNullFieldWasNullException => None
//		}
//	}

	def getValue[T](t: TableAliasInnerJoined[_])(f: t.Fields => DatabaseField[T]): T = {
		val c = t.wrappedFields(f)
		c.field.findValueInProtoStorable(ps, c).get
	}

	//
	def getValue[T](t: TableAliasOuterJoined[_])(f: t.Fields => DatabaseField[T]): Option[T] = {
		val c = t.wrappedFields(f)
		try {
			c.field.findValueInProtoStorable(ps, c)
		} catch {
			case _: NonNullFieldWasNullException => None
		}
	}
}
