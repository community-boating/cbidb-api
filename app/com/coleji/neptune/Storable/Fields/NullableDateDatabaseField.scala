package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_RELATIONAL, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, NullableDateColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NullableDateDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[LocalDate]](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case _: PERSISTENCE_SYSTEM_RELATIONAL => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[LocalDate]] = row.dateFields.get(key)



	def getValueFromString(s: String): Option[Option[LocalDate]] = {
		if (s == "") Some(None)
		else {
			try {
				Some(Some(LocalDate.parse(s, standardPattern)))
			} catch {
				case _: Throwable => None
			}
		}
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): NullableDateColumnAlias =
		NullableDateColumnAlias(tableAlias, this)

	def alias: NullableDateColumnAlias =
		NullableDateColumnAlias(entity.alias, this)
}