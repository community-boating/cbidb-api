package Storable.Fields

import Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

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

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[T]

	def isNull: Filter = Filter(t => s"$t.$getPersistenceFieldName IS NULL")

	def isNotNull: Filter = Filter(t => s"$t.$getPersistenceFieldName IS NOT NULL")

	def getValueFromString(s: String): Option[T]
}
