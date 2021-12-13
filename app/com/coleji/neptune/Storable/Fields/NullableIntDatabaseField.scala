package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.ColumnAlias
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableIntDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[Int]](entity, persistenceFieldName) {
	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[Int]] = row.intFields.get(key)

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "integer"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
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
