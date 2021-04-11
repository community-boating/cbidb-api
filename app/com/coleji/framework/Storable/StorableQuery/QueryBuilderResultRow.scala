package com.coleji.framework.Storable.StorableQuery

import com.coleji.framework.Storable.Fields.{DatabaseField, NonNullFieldWasNullException}
import com.coleji.framework.Storable.ProtoStorable

class QueryBuilderResultRow(val ps: ProtoStorable) {
	def getValue[T](field: ColumnAliasInnerJoined[_ <: DatabaseField[T]]): T =
		field.field.findValueInProtoStorable(ps, field).get


	def getValue[T](field: ColumnAliasOuterJoined[_ <: DatabaseField[T]]): Option[T] = {
		try {
			field.field.findValueInProtoStorable(ps, field)
		} catch {
			case e: NonNullFieldWasNullException => None
		}
	}
}
