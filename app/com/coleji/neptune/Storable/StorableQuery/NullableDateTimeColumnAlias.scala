package com.coleji.neptune.Storable.StorableQuery

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import com.coleji.neptune.Storable.Fields.{DatabaseField, NullableDateTimeDatabaseField}
import com.coleji.neptune.Storable.{Filter, StorableClass, StorableObject}

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

case class NullableDateTimeColumnAlias(override val table: TableAlias[_ <: StorableObject[_ <: StorableClass]], override val field: NullableDateTimeDatabaseField)
extends ColumnAlias[DatabaseField[Option[LocalDateTime]]](table, field) {
	def isYearConstant(year: Int)(implicit PA: PermissionsAuthority): Filter = PA.systemParams.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
			Filter(s"${table.name}.${field.persistenceFieldName} >= ${jan1.format(pattern)} AND ${table.name}.${field.persistenceFieldName} < ${nextJan1.format(pattern)}", List.empty)
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(s"TO_CHAR(${table.name}.${field.persistenceFieldName}, 'YYYY') = $year", List.empty)
	}
}
