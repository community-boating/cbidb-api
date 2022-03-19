package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.{ColumnAlias, DoubleColumnAlias, TableAlias}
import com.coleji.neptune.Storable.{ProtoStorable, StorableClass, StorableObject}

class DoubleDatabaseField(override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Double](entity, persistenceFieldName) {
	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "decimal"
		case PERSISTENCE_SYSTEM_ORACLE => "number"
	}

	def isNullable: Boolean = false

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Double] = {
		row.doubleFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null Double field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def getValueFromString(s: String): Option[Double] = {
		try {
			val d = s.toDouble
			Some(d)
		} catch {
			case _: Throwable => None
		}
	}

	def alias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): DoubleColumnAlias =
		DoubleColumnAlias(tableAlias, this)

	def alias: DoubleColumnAlias =
		DoubleColumnAlias(entity.alias, this)
}
