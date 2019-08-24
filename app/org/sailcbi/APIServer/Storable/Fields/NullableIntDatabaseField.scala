package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableIntDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) extends DatabaseField[Option[Int]](entity, persistenceFieldName) {
	def findValueInProtoStorable(row: ProtoStorable[String]): Option[Option[Int]] = row.intFields.get(this.getRuntimeFieldName)

	def getFieldType: String = PermissionsAuthority.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "integer"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def lessThanConstant(c: Int): Filter = {
		Filter(t => s"$t.$getPersistenceFieldName < $c")
	}

	def inList(l: List[Int]): Filter = {
		def groupIDs(ids: List[Int]): List[List[Int]] = {
			val MAX_IDS = 900
			if (ids.length <= MAX_IDS) List(ids)
			else {
				val splitList = ids.splitAt(MAX_IDS)
				splitList._1 :: groupIDs(splitList._2)
			}
		}

		if (l.isEmpty) Filter(t => "")
		else Filter(t => groupIDs(l).map(group => {
			s"$t.$getPersistenceFieldName in (${group.mkString(", ")})"
		}).mkString(" OR "))
	}

	def equalsConstant(i: Option[Int]): Filter = i match {
		case Some(x: Int) => Filter(t => s"$t.$getPersistenceFieldName = $i")
		case None => Filter(t => s"$t.$getPersistenceFieldName IS NULL")
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
