package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, NullableStringColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableStringDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String, fieldLength: Int) extends DatabaseField[Option[String]](entity, persistenceFieldName) {
	def getFieldLength: Int = fieldLength

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "varchar(" + getFieldLength + ")"
			case PERSISTENCE_SYSTEM_ORACLE => "varchar2(" + getFieldLength + ")"
		}
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[String]] = row.stringFields.get(key)

	def getValueFromString(s: String): Option[Option[String]] = if (s == "") Some(None) else Some(Some(s))

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): NullableStringColumnAlias =
		NullableStringColumnAlias(tableAlias, this)

	def alias: NullableStringColumnAlias =
		NullableStringColumnAlias(entity.alias, this)
}