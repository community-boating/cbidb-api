package Storable.Fields

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import CbiUtil._
import Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import Services._
import Storable.{Filter, ProtoStorable, StorableObject}

class DateDatabaseField(entity: StorableObject[_], persistenceFieldName: String) extends DatabaseField[LocalDate](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	def getFieldType: String = PermissionsAuthority.getPersistenceSystem match {
		case _: PERSISTENCE_SYSTEM_RELATIONAL => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable): Option[LocalDate] = {
		row.dateFields.get(this.getRuntimeFieldName) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new Exception("non-null Date field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def isYearConstant(year: Int): Filter = PermissionsAuthority.getPersistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			Filter(getFullyQualifiedName + ">= " + jan1.format(standardPattern) + " AND " + getFullyQualifiedName + " < " + nextJan1.format(standardPattern))
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter("TO_CHAR(" + getFullyQualifiedName + ", 'YYYY') = " + year)
	}

	def getValueFromString(s: String): Option[LocalDate] = {
		try {
			Some(LocalDate.parse(s, standardPattern))
		} catch {
			case _: Throwable => None
		}
	}

	private def dateComparison(date: LocalDate, comp: DateComparison): Filter = {
		val comparator: String = comp.comparator
		PermissionsAuthority.getPersistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL =>
				Filter(getFullyQualifiedName + " " + comparator + " '" + date.format(standardPattern) + "'")
			case PERSISTENCE_SYSTEM_ORACLE =>
				Filter("TRUNC(" + getFullyQualifiedName + ") " + comparator + " TO_DATE('" + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "','MM/DD/YYYY')")
		}
	}

	def isDateConstant(date: LocalDate): Filter = dateComparison(date, DATE_=)

	def greaterThanConstant(date: LocalDate): Filter = dateComparison(date, DATE_>)

	def lessThanConstant(date: LocalDate): Filter = dateComparison(date, DATE_<)

	def greaterEqualConstant(date: LocalDate): Filter = dateComparison(date, DATE_>=)

	def lessEqualConstant(date: LocalDate): Filter = dateComparison(date, DATE_<=)
}