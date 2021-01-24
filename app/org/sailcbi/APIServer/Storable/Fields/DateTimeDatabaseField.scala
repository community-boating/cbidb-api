package org.sailcbi.APIServer.Storable.Fields

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

class DateTimeDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[LocalDateTime](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	def isNullable: Boolean = false

	def getFieldType: String = PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "datetime"
		case PERSISTENCE_SYSTEM_ORACLE => "date"
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[LocalDateTime] = {
		row.dateTimeFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null DateTime field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def isYearConstant(year: Int): String => Filter = t => PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
			Filter(s"$t.$getPersistenceFieldName >= ${jan1.format(pattern)} AND $t.$getPersistenceFieldName < ${nextJan1.format(pattern)}", List.empty)
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(s"TO_CHAR($t.$getPersistenceFieldName, 'YYYY') = $year", List.empty)
	}

	def isDateConstant(date: LocalDate): String => Filter = t => PA.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL =>
			Filter(
				s"$t.$getPersistenceFieldName >= '${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}' AND " +
						s"$t.$getPersistenceFieldName < '${date.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}'",
				List.empty
			)
		case PERSISTENCE_SYSTEM_ORACLE =>
			Filter(s"TRUNC($t.$getPersistenceFieldName) = TO_DATE('${date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}','MM/DD/YYYY')", List.empty)
	}

	def getValueFromString(s: String): Option[LocalDateTime] = {
		try {
			Some(LocalDateTime.parse(s, standardPattern))
		} catch {
			case _: Throwable => None
		}
	}

//	def alias(tableAlias: TableAliasInnerJoined): ColumnAliasInnerJoined[LocalDateTime, DateTimeDatabaseField] = ColumnAliasInnerJoined(tableAlias, this)
//	def alias(tableAlias: TableAliasOuterJoined): ColumnAliasOuterJoined[LocalDateTime, DateTimeDatabaseField] = ColumnAliasOuterJoined(tableAlias, this)
}