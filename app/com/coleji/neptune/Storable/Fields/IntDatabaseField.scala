package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, IntColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class IntDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Int](entity, persistenceFieldName) {
	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "integer"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def isNullable: Boolean = false

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Int] = {
		row.intFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null Int field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def getValueFromString(s: String): Option[Int] = {
		try {
			val d = s.toInt
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}

	override def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): IntColumnAlias =
		IntColumnAlias(tableAlias, this)

	override def alias: IntColumnAlias =
		IntColumnAlias(entity.alias, this)
}
