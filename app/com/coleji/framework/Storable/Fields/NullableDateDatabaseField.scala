package com.coleji.framework.Storable.Fields

import com.coleji.framework.Core.PermissionsAuthority
import com.coleji.framework.Core.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE, PERSISTENCE_SYSTEM_RELATIONAL}
import com.coleji.framework.Storable.{Filter, ProtoStorable, StorableClass, StorableObject}
import com.coleji.framework.Util._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NullableDateDatabaseField(override val entity: StorableObject[_ <: StorableClass], persistenceFieldName: String)(implicit PA: PermissionsAuthority) extends DatabaseField[Option[LocalDate]](entity, persistenceFieldName) {
	val standardPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

	def isNullable: Boolean = true

	def getFieldType: String = PA.systemParams.persistenceSystem match {
		case _: PERSISTENCE_SYSTEM_RELATIONAL => "date"
	}

	def findValueInProtoStorableImpl[T](row: ProtoStorable[T], key: T): Option[Option[LocalDate]] = row.dateFields.get(key)

	def isYearConstant(year: Int): String => Filter = t => PA.systemParams.persistenceSystem match {
		case PERSISTENCE_SYSTEM_MYSQL => {
			val jan1 = LocalDate.of(year, 1, 1)
			val nextJan1 = LocalDate.of(year + 1, 1, 1)
			Filter(s"$t.$getPersistenceFieldName >= ${jan1.format(standardPattern)} AND $t.$getPersistenceFieldName < ${nextJan1.format(standardPattern)}", List.empty)
		}
		case PERSISTENCE_SYSTEM_ORACLE => Filter(s"TO_CHAR($t.$getPersistenceFieldName, 'YYYY') = $year", List.empty)
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

	private def dateComparison(date: LocalDate, comp: DateComparison): String => Filter = t => {
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

//	def alias(tableAlias: TableAliasInnerJoined): ColumnAliasInnerJoined[Option[LocalDate], NullableDateDatabaseField] = ColumnAliasInnerJoined(tableAlias, this)
//	def alias(tableAlias: TableAliasOuterJoined): ColumnAliasOuterJoined[Option[LocalDate], NullableDateDatabaseField] = ColumnAliasOuterJoined(tableAlias, this)
}