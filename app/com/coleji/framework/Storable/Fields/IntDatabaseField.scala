package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL, PersistenceSystem}
import com.coleji.framework.Storable.StorableQuery.ColumnAlias
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class IntDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) extends DatabaseField[Int](entity, persistenceFieldName) {
	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "integer"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def isNullable: Boolean = false

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Int] = {
		row.intFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null Int field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

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
				s"$t.$getPersistenceFieldName in (${group.mkString(", ")})",
				List.empty
			)))
		}
	}

	def lessThanConstant(c: Int): String => Filter = t => Filter(s"$t.$getPersistenceFieldName < $c", List.empty)

	def equalsConstant(i: Int): String => Filter = t => Filter(s"$t.$getPersistenceFieldName = $i", List.empty)

	def greaterThanConstant(c: Int): String => Filter = t => Filter(s"$t.$getPersistenceFieldName > $c", List.empty)

	def getValueFromString(s: String): Option[Int] = {
		try {
			val d = s.toInt
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}
}
