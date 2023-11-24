package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{BooleanDatabaseField, DatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class BooleanColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: BooleanDatabaseField)
extends ColumnAlias[DatabaseField[Boolean]](table, field) {

	def equalsConstant(b: Boolean): Filter = Filter(s"${table.name}.${field.persistenceFieldName} = '${if (b) "Y" else "N"}'", List.empty)
}
