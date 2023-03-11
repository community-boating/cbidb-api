package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class StringColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: StringDatabaseField)
	extends ColumnAlias[DatabaseField[String]](table, field) {

	def equalsConstant(c: String): Filter =
		Filter(s"${table.name}.${field.persistenceFieldName} = ?", List(c))


	def equalsConstantLowercase(c: String): Filter =
		Filter(s"lower(${table.name}.${field.persistenceFieldName}) = ?", List(c.toLowerCase()))


	def inList(ls: List[String]): Filter = Filter.or(groupValues(ls).map(group =>
		Filter(s"${table.name}.${field.persistenceFieldName} in (${group.map(_ => "?").mkString(", ")})", group)
	))

	def inListLowercase(ls: List[String]): Filter = Filter.or(groupValues(ls).map(group =>
		Filter(s"lower(${table.name}.${field.persistenceFieldName}) in (${group.map(_ => "?").mkString(", ")})", group.map(_.toLowerCase))
	))
}
