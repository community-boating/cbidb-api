package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.StorableQuery._
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

abstract class DatabaseField[T](val entity: StorableObject[_ <: StorableClass], val persistenceFieldName: String) {
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

	def getValueFromString(s: String): Option[T]

	def alias[U <: ColumnAlias[_]](tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): U
	def alias[U <: ColumnAlias[_]]: U
}

object DatabaseField {
	def testFilter(s: String): Filter = Filter("? = ?", List(s, s))
}
