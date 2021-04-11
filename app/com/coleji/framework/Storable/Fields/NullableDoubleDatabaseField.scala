package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.framework.Storable.StorableQuery.ColumnAlias
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableDoubleDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) extends DatabaseField[Option[Double]](entity, persistenceFieldName) {
	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[Double]] = row.doubleFields.get(key)

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def lessThanConstant(c: Double): String => Filter = t => {
		Filter(s"$t.$getPersistenceFieldName < $c", List.empty)
	}

	def inList(l: List[Double]): String => Filter = t => {
		def groupIDs(ids: List[Double]): List[List[Double]] = {
			val MAX_IDS = 900
			if (ids.length <= MAX_IDS) List(ids)
			else {
				val splitList = ids.splitAt(MAX_IDS)
				splitList._1 :: groupIDs(splitList._2)
			}
		}

		if (l.isEmpty) Filter.empty
		else Filter.or(groupIDs(l).map(group => Filter(
			s"$t.$getPersistenceFieldName in (${group.mkString(", ")})",
			List.empty
		)))
	}

	def equalsConstant(i: Option[Double]): String => Filter = t => i match {
		case Some(x: Double) => Filter(s"$t.$getPersistenceFieldName = $i", List.empty)
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL", List.empty)
	}

	def getValueFromString(s: String): Option[Option[Double]] = {
		if (s == "") Some(None)
		else {
			try {
				val d = s.toDouble
				Some(Some(d))
			} catch {
				case _: Throwable => None
			}
		}
	}
}
