package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Storable.StorableQuery.{ColumnAlias, ColumnAliasInnerJoined, TableAlias, TableAliasInnerJoined}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

abstract class DatabaseField[T](val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) {
	def getPersistenceFieldName: String = persistenceFieldName

	def getFieldType: String

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

	def findValueInProtoStorableImpl[U](row: ProtoStorable[U], key: U): Option[T]

//	def findValueInProtoStorableAliased(tableAlias: String, row: ProtoStorable[ColumnAliasInnerJoined[_, _]]): Option[T] = {
//		val ca = ColumnAliasInnerJoined[T, DatabaseField[T]](TableAliasInnerJoined(this.entity, tableAlias), this)
//		this.findValueInProtoStorableImpl[ColumnAliasInnerJoined[T, DatabaseField[T]]](
//			row.asInstanceOf[ProtoStorable[ColumnAliasInnerJoined[T, DatabaseField[T]]]],
//			ca
//		)
//	}

	def findValueInProtoStorable(ca: ColumnAlias[_, _], row: ProtoStorable[ColumnAlias[_, _]]): Option[T] =
		this.findValueInProtoStorableImpl(row, ca)

	def isNull: String => Filter = t => Filter(s"$t.$getPersistenceFieldName IS NULL", List.empty)

	def isNotNull: String => Filter = t => Filter(s"$t.$getPersistenceFieldName IS NOT NULL", List.empty)

	def equalsField[U <: DatabaseField[T]](c: ColumnAliasInnerJoined[T, U]): String => Filter = t => Filter(s"$t.$getPersistenceFieldName = ${c.table.name}.${c.field.getPersistenceFieldName}", List.empty)

	def getValueFromString(s: String): Option[T]
}

object DatabaseField {
	def testFilter(s: String): Filter = Filter("? = ?", List(s, s))
}