package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.PERSISTENCE_SYSTEM_RELATIONAL
import com.coleji.neptune.Storable.Fields.{DatabaseField, DoubleDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class DoubleColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: DoubleDatabaseField)
extends ColumnAlias[DatabaseField[Double]](table, field) {

	def lessThanConstant(c: Double): Filter = {
		Filter(s"${table.name}.${field.persistenceFieldName} < $c", List.empty)
	}

	def inList(l: List[Double])(implicit PA: PermissionsAuthority): Filter = PA.systemParams.persistenceSystem match {
		case r: PERSISTENCE_SYSTEM_RELATIONAL => {
			def groupIDs(ids: List[Double]): List[List[Double]] = {
				if (ids.length <= r.pbs.MAX_EXPR_IN_LIST) List(ids)
				else {
					val splitList = ids.splitAt(r.pbs.MAX_EXPR_IN_LIST)
					splitList._1 :: groupIDs(splitList._2)
				}
			}

			if (l.isEmpty) Filter.noneMatch
			else Filter.or(groupIDs(l).map(group => Filter(
				s"${table.name}.${field.persistenceFieldName} in (${group.mkString(", ")})",
				List.empty
			)))
		}
	}

	def equalsConstant(d: Double): Filter = Filter(s"${table.name}.${field.persistenceFieldName} = $d", List.empty)
}
