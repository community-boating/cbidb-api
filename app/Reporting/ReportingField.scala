package Reporting

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import Storable.Fields.FieldValue._
import Storable.Fields._
import Storable.StorableClass

class ReportingField[T <: StorableClass](
												val valueFunction: T => String,
												val fieldDisplayName: String,
												val isDefault: Boolean
										)

object ReportingField {
	def getReportingFieldFromDatabaseField[T <: StorableClass](
																	  f: DatabaseField[_],
																	  fieldDisplayName: String,
																	  isDefault: Boolean
															  ): ReportingField[T] = getReportingFieldFromDatabaseFieldParentObject[T, T](f, t => t, fieldDisplayName, isDefault)

	def getReportingFieldFromCalculatedValue[T <: StorableClass, U <: StorableClass](
																							getValue: (U => String),
																							getParent: (T => U),
																							fieldDisplayName: String,
																							isDefault: Boolean
																					): ReportingField[T] = {
		new ReportingField[T](
			(t: T) => getValue(getParent(t)),
			fieldDisplayName,
			isDefault
		)
	}

	def getReportingFieldFromDatabaseFieldParentObject[T <: StorableClass, U <: StorableClass](
																									  f: DatabaseField[_],
																									  getParent: (T => U),
																									  fieldDisplayName: String,
																									  isDefault: Boolean
																							  ): ReportingField[T] = f match {
		case i: IntDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).intValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: IntFieldValue) => v.get.toString
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: NullableIntDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).nullableIntValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: NullableIntFieldValue) => v.get match {
					case Some(vi: Int) => vi.toString
					case None => ""
				}
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: DoubleDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).doubleValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: DoubleFieldValue) => v.get.toString
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: NullableDoubleDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).nullableDoubleValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: NullableDoubleFieldValue) => v.get match {
					case Some(vi: Double) => vi.toString
					case None => ""
				}
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: StringDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).stringValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: StringFieldValue) => v.get.toString
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: NullableStringDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).nullableStringValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: NullableStringFieldValue) => v.get match {
					case Some(s: String) => s
					case _ => ""
				}
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: DateDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).dateValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: DateFieldValue) => v.get.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: NullableDateDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).nullableDateValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: NullableDateFieldValue) => v.get match {
					case Some(s: LocalDate) => s.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
					case _ => ""
				}
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case i: DateTimeDatabaseField => new ReportingField[T]((t: T) => {
			getParent(t).dateTimeValueMap.get(i.getRuntimeFieldName) match {
				case Some(v: DateTimeFieldValue) => v.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				case None => ""
			}
		}, fieldDisplayName, isDefault)
		case _ => throw new Exception("Unconfigured Reporting field type " + fieldDisplayName)
	}
}