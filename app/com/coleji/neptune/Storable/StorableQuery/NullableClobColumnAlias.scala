package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Storable.Fields.{DatabaseField, NullableClobDatabaseField}
import com.coleji.neptune.Storable.{StorableClass, StorableObject}

case class NullableClobColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: NullableClobDatabaseField)
extends ColumnAlias[DatabaseField[Option[String]]](table, field) {

}
