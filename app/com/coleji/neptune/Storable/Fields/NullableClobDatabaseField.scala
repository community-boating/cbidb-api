package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, NullableClobColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

class NullableClobDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[String]](entity, persistenceFieldName) {
	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "text"
		case PERSISTENCE_SYSTEM_ORACLE => "clob"

	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[String]] = row.stringFields.get(key)

	def getValueFromString(s: String): Option[Option[String]] = if (s == "") Some(None) else Some(Some(s))

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): NullableClobColumnAlias =
		NullableClobColumnAlias(tableAlias, this)

	def alias: NullableClobColumnAlias =
		NullableClobColumnAlias(entity.alias, this)
}