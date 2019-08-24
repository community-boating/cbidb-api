package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class IntDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Int](entity, persistenceFieldName) {
	def getFieldType: String = PA.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "integer"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[Int] = {
		println(row.intFields)
		println(this.getRuntimeFieldName)
		println(row.intFields.contains(this.getRuntimeFieldName))
		row.intFields.get(this.getRuntimeFieldName) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null Int field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def lessThanConstant(c: Int): Filter = {
		Filter(t => s"$t.$getPersistenceFieldName < $c")
	}

	def inList(l: List[Int]): Filter = PA.getPersistenceSystem match {
		case ps: PERSISTENCE_SYSTEM_RELATIONAL => {
			def groupIDs(ids: List[Int]): List[List[Int]] = {
				if (ids.length <= ps.pbs.MAX_EXPR_IN_LIST) List(ids)
				else {
					val splitList = ids.splitAt(ps.pbs.MAX_EXPR_IN_LIST)
					splitList._1 :: groupIDs(splitList._2)
				}
			}

			if (l.isEmpty) Filter(t => "")
			else Filter(t => groupIDs(l).map(group => {
				s"$t.$getPersistenceFieldName in (${group.mkString(", ")})"
			}).mkString(" OR "))
		}
	}

	def equalsConstant(i: Int): Filter = Filter(t => s"$t.$getPersistenceFieldName = $i")

	def getValueFromString(s: String): Option[Int] = {
		try {
			val d = s.toInt
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}
}
