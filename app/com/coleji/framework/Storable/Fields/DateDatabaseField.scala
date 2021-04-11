package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL, PersistenceSystem}
import com.coleji.framework.Storable.StorableQuery.ColumnAlias
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}
import com.coleji.framework.Util._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String) extends DatabaseField[LocalDate](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	def isNullable: Boolean = false

	def getFieldType(implicit persistenceSystem: PersistenceSystem): String = persistenceSystem match {
		case _: PERSISTENCE_SYSTEM_RELATIONAL => "date"
	}

	def findValueInProtoStorable(row: ProtoStorable, key: ColumnAlias[_]): Option[LocalDate] = {
		row.dateFields.get(key) match {
			case Some(Some(x)) => Some(x)
			case Some(None) => throw new NonNullFieldWasNullException("non-null Date field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
			case _ => None
		}
	}

	def isYearConstant(year: Int)(implicit PA: PermissionsAuthority): String => Filter = t => PA.systemParams.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			Filter(s"$t.$getPersistenceFieldName >= ${jan1.format(standardPattern)} AND $t.$getPersistenceFieldName < ${nextJan1.format(standardPattern)}", List.empty)
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(s"TO_CHAR($t.$getPersistenceFieldName, 'YYYY') = $year", List.empty)
	}

	def getValueFromString(s: String): Option[LocalDate] = {
		try {
			Some(LocalDate.parse(s, standardPattern))
		} catch {
			case _: Throwable => None
		}
	}

	private def dateComparison(date: LocalDate, comp: DateComparison)(implicit PA: PermissionsAuthority): String => Filter = t => {
		val comparator: String = comp.comparator
		PA.systemParams.persistenceSystem match {
			case PERSISTENCE_SYSTEM_MYSQL =>
				Filter(s"$t.$getPersistenceFieldName $comparator '${date.format(standardPattern)}'", List.empty)
			case PERSISTENCE_SYSTEM_ORACLE =>
				Filter(s"TRUNC($t.$getPersistenceFieldName) $comparator TO_DATE('${date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}','MM/DD/YYYY')", List.empty)
		}
	}

	def isDateConstant(date: LocalDate): String => Filter = dateComparison(date, DATE_=)

	def greaterThanConstant(date: LocalDate): String => Filter = dateComparison(date, DATE_>)

	def lessThanConstant(date: LocalDate): String => Filter = dateComparison(date, DATE_<)

	def greaterEqualConstant(date: LocalDate): String => Filter = dateComparison(date, DATE_>=)

	def lessEqualConstant(date: LocalDate): String => Filter = dateComparison(date, DATE_<=)
}