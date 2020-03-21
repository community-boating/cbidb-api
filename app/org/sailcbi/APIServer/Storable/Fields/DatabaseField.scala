package org.sailcbi.APIServer.Storable.Fields

import java.time.LocalDate

import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, TableAlias}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

abstract class DatabaseField[T](val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) {
	def getPersistenceFieldName: String = persistenceFieldName

	def getFieldType: String

	private var runtimeFieldName: Option[String] = None

	def getRuntimeFieldName: String = runtimeFieldName match {
		case None => throw new Exception("Runtime field name never got set for " + entity.entityName + "." + persistenceFieldName)
		case Some(s: String) => s
	}

	def setRuntimeFieldName(s: String): Unit = runtimeFieldName match {
		case Some(_) => throw new Exception("Multiple calls to set runtimeFieldName for " + entity.entityName + "." + persistenceFieldName)
		case None => runtimeFieldName = Some(s)
	}

	def findValueInProtoStorableImpl[U](row: ProtoStorable[U], key: U): Option[T]

	def findValueInProtoStorableAliased(tableAlias: String, row: ProtoStorable[ColumnAlias[_, _]]): Option[T] = {
		val ca = ColumnAlias[T, DatabaseField[T]](TableAlias(this.entity, tableAlias), this)
		this.findValueInProtoStorableImpl[ColumnAlias[T, DatabaseField[T]]](
			row.asInstanceOf[ProtoStorable[ColumnAlias[T, DatabaseField[T]]]],
			ca
		)
	}

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[T] =
		this.findValueInProtoStorableImpl(row, this.getRuntimeFieldName)

	def isNull: String => Filter = t => Filter(s"$t.$getPersistenceFieldName IS NULL")

	def isNotNull: String => Filter = t => Filter(s"$t.$getPersistenceFieldName IS NOT NULL")

	def getValueFromString(s: String): Option[T]
}
