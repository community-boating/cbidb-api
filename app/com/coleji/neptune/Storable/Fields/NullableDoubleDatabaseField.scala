package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, NullableDateTimeColumnAlias, NullableDoubleColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableDoubleDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[Double]](entity, persistenceFieldName) {
	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[Double]] = row.doubleFields.get(key)

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
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

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): NullableDoubleColumnAlias =
		NullableDoubleColumnAlias(tableAlias, this)

	def alias: NullableDoubleColumnAlias =
		NullableDoubleColumnAlias(entity.alias, this)
}
