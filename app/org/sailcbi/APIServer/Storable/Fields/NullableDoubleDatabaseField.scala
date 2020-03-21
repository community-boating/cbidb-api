package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, TableAlias}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableDoubleDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Option[Double]](entity, persistenceFieldName) {
	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Option[Double]] = row.doubleFields.get(key)

	def getFieldType: String = PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def lessThanConstant(c: Double): String => Filter = t => {
		Filter(s"$t.$getPersistenceFieldName < $c")
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

		if (l.isEmpty) Filter("")
		else Filter(groupIDs(l).map(group => {
			s"$t.$getPersistenceFieldName in (${group.mkString(", ")})"
		}).mkString(" OR "))
	}

	def equalsConstant(i: Option[Double]): String => Filter = t => i match {
		case Some(x: Double) => Filter(s"$t.$getPersistenceFieldName = $i")
		case None => Filter(s"$t.$getPersistenceFieldName IS NULL")
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

	def alias(tableAlias: TableAlias): ColumnAlias[Option[Double], NullableDoubleDatabaseField] = ColumnAlias(tableAlias, this)
}
