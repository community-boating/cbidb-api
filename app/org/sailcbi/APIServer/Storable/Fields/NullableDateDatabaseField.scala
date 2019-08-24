package org.sailcbi.APIServer.Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}

class NullableDateDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Option[LocalDate]](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	def getFieldType: String = PA.getPersistenceSystem match {
		case _: PERSISTENCE_SYSTEM_RELATIONAL => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable[String]): Option[Option[LocalDate]] = row.dateFields.get(this.getRuntimeFieldName)

	def isYearConstant(year: Int): Filter = PA.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			Filter(t => s"$t.$getPersistenceFieldName >= ${jan1.format(standardPattern)} AND $t.$getPersistenceFieldName < ${nextJan1.format(standardPattern)}")
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(t => s"TO_CHAR($t.$getPersistenceFieldName, 'YYYY') = $year")
	}

	def getValueFromString(s: String): Option[Option[LocalDate]] = {
		if (s == "") Some(None)
		else {
			try {
				Some(Some(LocalDate.parse(s, standardPattern)))
			} catch {
				case _: Throwable => None
			}
		}
	}

	private def dateComparison(date: LocalDate, comp: DateComparison): Filter = {
		val comparator: String = comp.comparator
		PA.getPersistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL =>
				Filter(t => s"$t.$getPersistenceFieldName $comparator '${date.format(standardPattern)}'")
			case PERSISTENCE_SYSTEM_ORACLE =>
				Filter(t => s"TRUNC($t.$getPersistenceFieldName) $comparator TO_DATE('${date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}','MM/DD/YYYY')")
		}
	}

	def isDateConstant(date: LocalDate): Filter = dateComparison(date, DATE_=)

	def greaterThanConstant(date: LocalDate): Filter = dateComparison(date, DATE_>)

	def lessThanConstant(date: LocalDate): Filter = dateComparison(date, DATE_<)

	def greaterEqualConstant(date: LocalDate): Filter = dateComparison(date, DATE_>=)

	def lessEqualConstant(date: LocalDate): Filter = dateComparison(date, DATE_<=)
}