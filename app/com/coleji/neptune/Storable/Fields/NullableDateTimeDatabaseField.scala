package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, NullableDateTimeColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NullableDateTimeDatabaseField (override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[LocalDateTime]](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "datetime"
		case PERSISTENCE_SYSTEM_ORACLE => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[LocalDateTime]] = row.dateTimeFields.get(key)

	def getValueFromString(s: String): Option[Option[LocalDateTime]] = {
		if (s == "") Some(None)
		else {
			try {
				Some(Some(LocalDateTime.parse(s, standardPattern)))
			} catch {
				case _: Throwable => None
			}
		}
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): NullableDateTimeColumnAlias =
		NullableDateTimeColumnAlias(tableAlias, this)

	def alias: NullableDateTimeColumnAlias =
		NullableDateTimeColumnAlias(entity.alias, this)
}
