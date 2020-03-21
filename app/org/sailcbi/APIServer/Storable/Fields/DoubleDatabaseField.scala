package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, TableAlias}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class DoubleDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Double](entity, persistenceFieldName) {
	def getFieldType: String = PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Double] = {
		row.doubleFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null Double field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def lessThanConstant(c: Double): String => Filter = {
		t => Filter(s"$t.$getPersistenceFieldName < $c")
	}

	def inList(l: List[Double]): String => Filter = t => PA.persistenceSystem match {
		case r: PERSISTENCE_SYSTEM_RELATIONAL => {
			def groupIDs(ids: List[Double]): List[List[Double]] = {
				if (ids.length <= r.pbs.MAX_EXPR_IN_LIST) List(ids)
				else {
					val splitList = ids.splitAt(r.pbs.MAX_EXPR_IN_LIST)
					splitList._1 :: groupIDs(splitList._2)
				}
			}

			if (l.isEmpty) Filter("")
			else Filter(groupIDs(l).map(group => {
				s"$t.$getPersistenceFieldName in (${group.mkString(", ")})"
			}).mkString(" OR "))
		}
	}

	def equalsConstant(d: Double): String => Filter = t => Filter(s"$t.$getPersistenceFieldName = $d")

	def getValueFromString(s: String): Option[Double] = {
		try {
			val d = s.toDouble
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}

	def alias(tableAlias: TableAlias): ColumnAlias[Double, DoubleDatabaseField] = ColumnAlias(tableAlias, this)
}
