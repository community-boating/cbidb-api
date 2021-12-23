package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, NullableDoubleDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class NullableDoubleColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: NullableDoubleDatabaseField)
extends ColumnAlias[DatabaseField[Option[Double]]](table, field) {
	def lessThanConstant(c: Double): Filter = {
		Filter(s"${table.name}.${field.persistenceFieldName} < $c", List.empty)
	}

	def inList(l: List[Double]): Filter = {
		def groupIDs(ids: List[Double]): List[List[Double]] = {
			val MAX_IDS = 900
			if (ids.length <= MAX_IDS) List(ids)
			else {
				val splitList = ids.splitAt(MAX_IDS)
				splitList._1 :: groupIDs(splitList._2)
			}
		}

		if (l.isEmpty) Filter.noneMatch
		else Filter.or(groupIDs(l).map(group => Filter(
			s"${table.name}.${field.persistenceFieldName} in (${group.mkString(", ")})",
			List.empty
		)))
	}

	def equalsConstant(i: Option[Double]): Filter = i match {
		case Some(x: Double) => Filter(s"${table.name}.${field.persistenceFieldName} = $i", List.empty)
		case None => Filter(s"${table.name}.${field.persistenceFieldName} IS NULL", List.empty)
	}
}
