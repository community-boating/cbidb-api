package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, NullableBooleanColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

class NullableBooleanDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[Boolean]](entity, persistenceFieldName) {
	def getFieldLength: Int = 1

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = getFieldLength match {
		case l if l == 1 => "char(" + getFieldLength + ")"
		case _ => persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL => "char(1)"
			case PERSISTENCE_SYSTEM_ORACLE => "char(1)"
		}
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[Boolean]] = {
		row.stringFields.get(key) match {
			case Some(Some("Y")) => Some(Some(true))
			case Some(Some("N")) => Some(Some(false))
			case Some(None) => Some(None)
			case _ => None
		}
	}

	def getValueFromString(s: String): Option[Option[Boolean]] = s.toLowerCase match {
		case "true" => Some(Some(true))
		case "false" => Some(Some(false))
		case "" => Some(None)
		case _ => None
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): NullableBooleanColumnAlias =
		NullableBooleanColumnAlias(tableAlias, this)

	def alias: NullableBooleanColumnAlias =
		NullableBooleanColumnAlias(entity.alias, this)
}