package Storable.StorableQuery

import Entities.EntityDefinitions.JpClassType
import Storable.Fields.{IntDatabaseField, StringDatabaseField}

class Query(fields: List[ColumnAlias[_]], joinPoints: List[JoinPoint[_]]) {
	var intValues: Map[ColumnAlias[Int], Int] = Map()
	var stringValues: Map[ColumnAlias[String], String] = Map()

	fields.foreach(field => field.field match {
		case a: IntDatabaseField => intValues += (field.asInstanceOf[ColumnAlias[Int]] -> 4)
		case b: StringDatabaseField => stringValues += (field.asInstanceOf[ColumnAlias[String]] -> "b")
	})

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