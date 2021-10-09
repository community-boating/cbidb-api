package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.ColumnAlias
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class DoubleDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) extends DatabaseField[Double](entity, persistenceFieldName) {
	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def isNullable: Boolean = false

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Double] = {
		row.doubleFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null Double field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def lessThanConstant(c: Double): String => Filter = {
		t => Filter(s"$t.$getPersistenceFieldName < $c", List.empty)
	}

	def inList(l: List[Double])(implicit PA: PermissionsAuthority): String => Filter = t => PA.systemParams.persistenceSystem match {
		case r: PERSISTENCE_SYSTEM_RELATIONAL => {
			def groupIDs(ids: List[Double]): List[List[Double]] = {
				if (ids.length <= r.pbs.MAX_EXPR_IN_LIST) List(ids)
				else {
					val splitList = ids.splitAt(r.pbs.MAX_EXPR_IN_LIST)
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

	def equalsConstant(d: Double): String => Filter = t => Filter(s"$t.$getPersistenceFieldName = $d", List.empty)

	def getValueFromString(s: String): Option[Double] = {
		try {
			val d = s.toDouble
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}
}
