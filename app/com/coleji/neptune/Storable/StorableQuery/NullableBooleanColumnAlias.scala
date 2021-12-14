package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, NullableBooleanDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

case class NullableBooleanColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: NullableBooleanDatabaseField)
	extends ColumnAlias[DatabaseField[Option[Boolean]]](table, field) {

	def equals(b: Option[Boolean]): Filter = b match {
		case Some(x) => Filter(s"${table.name}.${field.persistenceFieldName} = '${if (x) "Y" else "N"}'", List.empty)
		case None => Filter(s"${table.name}.${field.persistenceFieldName} IS NULL", List.empty)
	}
}
