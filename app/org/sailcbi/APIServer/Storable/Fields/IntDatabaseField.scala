package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, TableAlias}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class IntDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Int](entity, persistenceFieldName) {
	def getFieldType: String = PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "integer"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Int] = {
		row.intFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null Int field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def inList(l: List[Int]): String => Filter = t => PA.persistenceSystem match {
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

	def alias(tableAlias: TableAlias): ColumnAlias[Int, IntDatabaseField] = ColumnAlias(tableAlias, this)
}
