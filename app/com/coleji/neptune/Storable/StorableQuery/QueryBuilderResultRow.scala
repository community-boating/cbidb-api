package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, NonNullFieldWasNullException}
import com.coleji.neptune.Storable.{ProtoStorable, StorableObject}

class QueryBuilderResultRow(val ps: ProtoStorable) {
	def getValue[T](t: TableAliasInnerJoined[_])(f: t.Fields => DatabaseField[T]): T = {
		val c = t.wrappedFields(f)
		innerGetValue(c, ps)
	}

	def getValue[T](t: TableAliasOuterJoined[_])(f: t.Fields => DatabaseField[T]): Option[T] = {
		val c = t.wrappedFields(f)
		try {
			Some(innerGetValue(c, ps))
		} catch {
			case _: NonNullFieldWasNullException => None
		}
	}

	private def innerGetValue[T](c: ColumnAlias[DatabaseField[T]], ps: ProtoStorable): T = {
		// TODO: can we detect that an unselected field is being referenced
		c.field.findValueInProtoStorable(ps, c) match {
			case None => throw new Exception("QB attempted to reference unselected field: " + c)
			case Some(s) => s
		}
	}
}
