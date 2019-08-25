package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class DoubleDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Double](entity, persistenceFieldName) {
	def getFieldType: String = PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[Double] = {
		row.doubleFields.get(this.getRuntimeFieldName) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null Double field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def lessThanConstant(c: Double): Filter = {
		Filter(t => s"$t.$getPersistenceFieldName < $c")
	}

	def inList(l: List[Double]): Filter = PA.persistenceSystem match {
		case r: PERSISTENCE_SYSTEM_RELATIONAL => {
			def groupIDs(ids: List[Double]): List[List[Double]] = {
				if (ids.length <= r.pbs.MAX_EXPR_IN_LIST) List(ids)
				else {
					val splitList = ids.splitAt(r.pbs.MAX_EXPR_IN_LIST)
					splitList._1 :: groupIDs(splitList._2)
				}
			}

			if (l.isEmpty) Filter(t => "")
			else Filter(t => groupIDs(l).map(group => {
				s"$t.$getPersistenceFieldName in (${group.mkString(", ")})"
			}).mkString(" OR "))
		}
	}

	def equalsConstant(d: Double): Filter = Filter(t => s"$t.$getPersistenceFieldName = $d")

	def getValueFromString(s: String): Option[Double] = {
		try {
			val d = s.toDouble
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}
}
