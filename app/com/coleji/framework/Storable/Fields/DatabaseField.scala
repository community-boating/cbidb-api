package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Storable.StorableQuery._
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

abstract class DatabaseField[T](val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) {
	def getPersistenceFieldName: String = persistenceFieldName

	type ValueType = T

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String

	private var runtimeFieldName: Option[String] = None

	def isNullable: Boolean

	def getRuntimeFieldName: String = runtimeFieldName match {
		case None => throw new Exception("Runtime field name never got set for " + entity.entityName + "." + persistenceFieldName)
		case Some(s: String) => s
	}

	def setRuntimeFieldName(s: String): Unit = runtimeFieldName match {
		case Some(_) => throw new Exception("Multiple calls to set runtimeFieldName for " + entity.entityName + "." + persistenceFieldName)
		case None => runtimeFieldName = Some(s)
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[T]

	def isNull: String => Filter = t => Filter(s"$t.$getPersistenceFieldName IS NULL", List.empty)

	def isNotNull: String => Filter = t => Filter(s"$t.$getPersistenceFieldName IS NOT NULL", List.empty)

	def equalsField[U <: DatabaseField[T]](c: ColumnAlias[U]): String => Filter = t => Filter(s"$t.$getPersistenceFieldName = ${c.table.name}.${c.field.getPersistenceFieldName}", List.empty)

	def getValueFromString(s: String): Option[T]

	def alias(tableAlias: TableAliasInnerJoined[_ <: StorableObject[_ <: StorableClass]]): ColumnAliasInnerJoined[this.type] = ColumnAliasInnerJoined(tableAlias, this)
	def alias(tableAlias: TableAliasOuterJoined[_ <: StorableObject[_ <: StorableClass]]): ColumnAliasOuterJoined[this.type] = ColumnAliasOuterJoined(tableAlias, this)
}

object DatabaseField {
	def testFilter(s: String): Filter = Filter("? = ?", List(s, s))
}
