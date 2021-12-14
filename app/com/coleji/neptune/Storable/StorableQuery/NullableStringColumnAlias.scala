package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, NullableStringDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class NullableStringColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: NullableStringDatabaseField)
extends ColumnAlias[DatabaseField[Option[String]]](table, field) {
	def equalsConstant(os: Option[String]): Filter = os match {
		case Some(s: String) => Filter(s"${table.name}.${field.persistenceFieldName} = ?", List(s))
		case None => Filter(s"${table.name}.${field.persistenceFieldName} IS NULL", List.empty)
	}

	def equalsConstantLowercase(os: Option[String]): Filter = os match {
		case Some(s: String) => Filter(s"lower(${table.name}.${field.persistenceFieldName}) = ?", List(s.toLowerCase()))
		case None => Filter(s"${table.name}.${field.persistenceFieldName} IS NULL", List.empty)
	}
}
