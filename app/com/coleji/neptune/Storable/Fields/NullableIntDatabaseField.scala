package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.ColumnAlias
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableIntDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) extends DatabaseField[Option[Int]](entity, persistenceFieldName) {
	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[Int]] = row.intFields.get(key)

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "integer"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def lessThanConstant(c: Int): String => Filter = t => {
		Filter(s"$t.$getPersistenceFieldName < $c", List.empty)
	}

	def inList(l: List[Int]): String => Filter = t => {
		def groupIDs(ids: List[Int]): List[List[Int]] = {
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

	def equalsConstant(i: Option[Int]): String => Filter = t => i match {
		case Some(x: Int) => Filter(s"$t.$getPersistenceFieldName = $i", List.empty)
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL", List.empty)
	}

	def getValueFromString(s: String): Option[Option[Int]] = {
		if (s == "") Some(None)
		else {
			try {
				val d = s.toInt
				Some(Some(d))
			} catch {
				case _: Throwable => None
			}
		}
	}
}
