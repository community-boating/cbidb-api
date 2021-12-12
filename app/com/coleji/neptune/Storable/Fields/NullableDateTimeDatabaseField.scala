package com.coleji.neptune.Storable.Fields

import com.coleji.neptune.Core.PermissionsAuthority
import com.coleji.neptune.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.neptune.Storable.StorableQuery.ColumnAlias
import com.coleji.neptune.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

class NullableDateTimeDatabaseField (override val entity: StorableObject[_ <: StorableClass], override val persistenceFieldName: String) extends DatabaseField[Option[LocalDateTime]](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	def isNullable: Boolean = true

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "datetime"
		case PERSISTENCE_SYSTEM_ORACLE => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[Option[LocalDateTime]] = row.dateTimeFields.get(key)

	def isYearConstant(year: Int)(implicit PA: PermissionsAuthority): String => Filter = t => PA.systemParams.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
			Filter(s"$t.$persistenceFieldName >= ${jan1.format(pattern)} AND $t.$persistenceFieldName < ${nextJan1.format(pattern)}", List.empty)
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(s"TO_CHAR($t.$persistenceFieldName, 'YYYY') = $year", List.empty)
	}

	def getValueFromString(s: String): Option[Option[LocalDateTime]] = {
		if (s == "") Some(None)
		else {
			try {
				Some(Some(LocalDateTime.parse(s, standardPattern)))
			} catch {
				case _: Throwable => None
			}
		}
	}
}