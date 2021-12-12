package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.PERSISTENCE_SYSTEM_RELATIONAL
import com.coleji.neptune.Storable.Fields.{DatabaseField, IntDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class IntColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: IntDatabaseField)
extends ColumnAlias[DatabaseField[Int]](table, field) {
	def inList(l: List[Int])(implicit PA: PermissionsAuthority): String => Filter = t => PA.systemParams.persistenceSystem match {
		case ps: PERSISTENCE_SYSTEM_RELATIONAL => {
			def groupIDs(ids: List[Int]): List[List[Int]] = {
				if (ids.length <= ps.pbs.MAX_EXPR_IN_LIST) List(ids)
				else {
					val splitList = ids.splitAt(ps.pbs.MAX_EXPR_IN_LIST)
					splitList._1 :: groupIDs(splitList._2)
				}
			}

			if (l.isEmpty) Filter.empty
			else Filter.or(groupIDs(l).map(group => Filter(
				s"$t.${field.persistenceFieldName} in (${group.mkString(", ")})",
				List.empty
			)))
		}
	}

	def lessThanConstant(c: Int): Filter = Filter(s"${table.name}.${field.persistenceFieldName} < $c", List.empty)

	def equalsConstant(i: Int): Filter = Filter(s"${table.name}.${field.persistenceFieldName} = $i", List.empty)

	def greaterThanConstant(c: Int): Filter = Filter(s"${table.name}.${field.persistenceFieldName} > $c", List.empty)
}
