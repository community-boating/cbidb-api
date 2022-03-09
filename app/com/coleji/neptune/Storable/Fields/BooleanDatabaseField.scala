package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_RELATIONAL, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{BooleanColumnAlias, ColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

class BooleanDatabaseField(
	override val entity: StorableObject[_ <: StorableClass],
	override val persistenceFieldName: String,
	nullImpliesFalse: Boolean = false
) extends DatabaseField[Boolean](entity, persistenceFieldName) {
	def getFieldLength: Int = 1

	def isNullable: Boolean = nullImpliesFalse

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = getFieldLength match {
		case _ => persistenceSystem match {
			case _: PERSISTENCE_SYSTEM_RELATIONAL => "char(1)"
		}
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Boolean] = {
		row.stringFields.get(key) match {
			case Some(Some("Y")) => Some(true)
			case Some(Some("N")) => Some(false)
			case Some(None) =>
				if (nullImpliesFalse) Some(false)
				else throw new NonNullFieldWasNullException("non-null Boolean field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def getValueFromString(s: String): Option[Boolean] = s.toLowerCase match {
		case "true" => Some(true)
		case "false" => Some(false)
		case _ => None
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): BooleanColumnAlias =
		BooleanColumnAlias(tableAlias, this)

	def alias: BooleanColumnAlias =
		BooleanColumnAlias(entity.alias, this)
}