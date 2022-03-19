package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, DateTimeColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[LocalDateTime](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	def isNullable: Boolean = false

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "datetime"
		case PERSISTENCE_SYSTEM_ORACLE => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[LocalDateTime] = {
		row.dateTimeFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null DateTime field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def getValueFromString(s: String): Option[LocalDateTime] = {
		try {
			Some(LocalDateTime.parse(s, standardPattern))
		} catch {
			case _: Throwable => None
		}
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): DateTimeColumnAlias =
		DateTimeColumnAlias(tableAlias, this)

	def alias: DateTimeColumnAlias =
		DateTimeColumnAlias(entity.alias, this)
}