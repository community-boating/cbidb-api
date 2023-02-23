package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_RELATIONAL, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, DateColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[LocalDate](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	def isNullable: Boolean = false

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case _: PERSISTENCE_SYSTEM_RELATIONAL => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[LocalDate] = {
		row.dateFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null Date field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}



	def getValueFromString(s: String): Option[LocalDate] = {
		try {
			Some(LocalDate.parse(s, standardPattern))
		} catch {
			case _: Throwable => None
		}
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): DateColumnAlias =
		DateColumnAlias(tableAlias, this)

	def alias: DateColumnAlias =
		DateColumnAlias(entity.alias, this)
}
