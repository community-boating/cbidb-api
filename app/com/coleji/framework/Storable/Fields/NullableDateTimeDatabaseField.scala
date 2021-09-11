package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PersistenceSystem}
import com.coleji.framework.Storable.StorableQuery.ColumnAlias
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

class NullableDateTimeDatabaseField (override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) extends DatabaseField[Option[LocalDateTime]](entity, persistenceFieldName) {
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
			Filter(s"$t.$getPersistenceFieldName >= ${jan1.format(pattern)} AND $t.$getPersistenceFieldName < ${nextJan1.format(pattern)}", List.empty)
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(s"TO_CHAR($t.$getPersistenceFieldName, 'YYYY') = $year", List.empty)
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