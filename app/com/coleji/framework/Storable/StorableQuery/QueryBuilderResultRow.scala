package com.coleji.framework.Storable.StorableQuery

import com.coleji.framework.Storable.Fields.{DatabaseField, NonNullFieldWasNullException}
import com.coleji.framework.Storable.ProtoStorable

class QueryBuilderResultRow(val ps: ProtoStorable[ColumnAlias[_, _]]) {
	def getValue[T](field: ColumnAliasInnerJoined[T, _ <: DatabaseField[T]]): T =
		field.field.findValueInProtoStorable(field, ps).get


	def getValue[T](field: ColumnAliasOuterJoined[T, _ <: DatabaseField[T]]): Option[T] = {
		try {
			field.field.findValueInProtoStorable(field, ps)
		} catch {
			case e: NonNullFieldWasNullException => None
		}
	}
}
