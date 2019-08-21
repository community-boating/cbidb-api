package Storable.StorableQuery

import java.time.{LocalDate, LocalDateTime}

import Storable.Fields.{IntDatabaseField, StringDatabaseField}

class QueryBuilderResultRow(
								   intValues: Map[ColumnAlias[Int], Option[Int]],
//	doubleFields: Map[ColumnAlias[Double], Option[Double]],
								   stringValues: Map[ColumnAlias[String], Option[String]],
//	dateFields: Map[ColumnAlias[LocalDate], Option[LocalDate]],
//	dateTimeFields: Map[ColumnAlias[LocalDateTime], Option[LocalDateTime]]
) {
//	fields.foreach(field => field.field match {
//		case a: IntDatabaseField => intValues += (field.asInstanceOf[ColumnAlias[Int]] -> 4)
//		case b: StringDatabaseField => stringValues += (field.asInstanceOf[ColumnAlias[String]] -> "b")
//	})

	def getValue[T](field: ColumnAlias[T]): T = field.field match {
		case a: IntDatabaseField => {
			println(field.field.getPersistenceFieldName + " is an int!")
			intValues(field.asInstanceOf[ColumnAlias[Int]]).asInstanceOf[T]
		}
		case b: StringDatabaseField => {
			println(field.field.getPersistenceFieldName + " is a string!")
			stringValues(field.asInstanceOf[ColumnAlias[String]]).asInstanceOf[T]
		}
	}
}
