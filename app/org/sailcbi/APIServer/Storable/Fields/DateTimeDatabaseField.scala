package org.sailcbi.APIServer.Storable.Fields

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class DateTimeDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[LocalDateTime](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	def getFieldType: String = PA.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => "datetime"
		case PERSISTENCE_SYSTEM_ORACLE => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[LocalDateTime] = {
		row.dateTimeFields.get(this.getRuntimeFieldName) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null DateTime field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def isYearConstant(year: Int): Filter = PA.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
			Filter(t => s"$t.$getPersistenceFieldName >= ${jan1.format(pattern)} AND $t.$getPersistenceFieldName < ${nextJan1.format(pattern)}")
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(t => s"TO_CHAR($t.$getPersistenceFieldName, 'YYYY') = $year")
	}

	def isDateConstant(date: LocalDate): Filter = PA.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL =>
			Filter(t =>
				s"$t.$getPersistenceFieldName >= '${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}' AND " +
						s"$t.$getPersistenceFieldName < '${date.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}'"
			)
		case PERSISTENCE_SYSTEM_ORACLE =>
			Filter(t => s"TRUNC($t.$getPersistenceFieldName) = TO_DATE('${date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}','MM/DD/YYYY')")
	}

	def getValueFromString(s: String): Option[LocalDateTime] = {
		try {
			Some(LocalDateTime.parse(s, standardPattern))
		} catch {
			case _: Throwable => None
		}
	}
}