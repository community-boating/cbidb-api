package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.neptune.Storable.StorableQuery._
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

abstract class DatabaseField[T](val entity: StorableObject[_ <: StorableClass], val persistenceFieldName: String) extends Serializable {
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

	def abstractAlias: ColumnAlias[_] = abstractAlias(None)
	def abstractAlias(tableAlias: TableAlias[_ <: StorableObject[_ <: StorableClass]]): ColumnAlias[_] = abstractAlias(Some(tableAlias))
	def abstractAlias(tableAlias: Option[TableAlias[_ <: StorableObject[_ <: StorableClass]]]): ColumnAlias[_] = {
		tableAlias match {
			case Some(ta) => {
				this match {
					case b: BooleanDatabaseField => b.alias(ta)
					case d: DateDatabaseField => d.alias(ta)
					case dt: DateTimeDatabaseField => dt.alias(ta)
					case d: DoubleDatabaseField => d.alias(ta)
					case i: IntDatabaseField => i.alias(ta)
					case s: StringDatabaseField => s.alias(ta)

					case b: NullableBooleanDatabaseField => b.alias(ta)
					case c: NullableClobDatabaseField => c.alias(ta)
					case d: NullableDateDatabaseField => d.alias(ta)
					case dt: NullableDateTimeDatabaseField => dt.alias(ta)
					case d: NullableDoubleDatabaseField => d.alias(ta)
					case i: NullableIntDatabaseField => i.alias(ta)
					case s: NullableStringDatabaseField => s.alias(ta)
				}
			}
			case None => {
				this match {
					case b: BooleanDatabaseField => b.alias
					case d: DateDatabaseField => d.alias
					case dt: DateTimeDatabaseField => dt.alias
					case d: DoubleDatabaseField => d.alias
					case i: IntDatabaseField => i.alias
					case s: StringDatabaseField => s.alias

					case b: NullableBooleanDatabaseField => b.alias
					case c: NullableClobDatabaseField => c.alias
					case d: NullableDateDatabaseField => d.alias
					case dt: NullableDateTimeDatabaseField => dt.alias
					case d: NullableDoubleDatabaseField => d.alias
					case i: NullableIntDatabaseField => i.alias
					case s: NullableStringDatabaseField => s.alias
				}
			}
		}
	}
}

object DatabaseField {
	def testFilter(s: String): Filter = Filter("? = ?", List(s, s))
}
