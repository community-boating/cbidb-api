package org.sailcbi.APIServer.Entities

import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForUpdateOrDelete, PreparedQueryForInsert, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.PersistenceBroker

trait CastableToStorableClass {
	val storableObject: CastableToStorableObject[_]
	val persistenceValues: Map[String, String]
	val pkSqlLiteral: String

	def getInsertPreparedQuery: PreparedQueryForInsert = new PreparedQueryForInsert(storableObject.allowedUserTypes, true) {
		override val pkName: Option[String] = Some(storableObject.pkColumnName)
		val columnNamesAndValues: List[(String, String)] = persistenceValues.toList
		val columnNames: String = columnNamesAndValues.map(_._1).mkString(", ")
		val values: List[String] = columnNamesAndValues.map(_._2)


		override def getQuery: String =
			s"""
			   |insert into ${storableObject.apexTableName} ($columnNames) values (${values.map(_ => "?").mkString(", ")})
			   |
      """.stripMargin

		override val params: List[String] = values

	}

	def getUpdatePreparedQuery: PreparedQueryForUpdateOrDelete = new PreparedQueryForUpdateOrDelete(storableObject.allowedUserTypes, true) {
		val setStatements: String = persistenceValues.toList.map(t => t._1 + " = ?").mkString(", ")
		val values: List[String] = persistenceValues.toList.map(t => t._2)


		override def getQuery: String =
			s"""
			   |update ${storableObject.apexTableName} set $setStatements where ${storableObject.pkColumnName} = $pkSqlLiteral
			   |
      """.stripMargin

		override val params: List[String] = values
	}

	def getDeletePreparedQuery: HardcodedQueryForUpdateOrDelete = new HardcodedQueryForUpdateOrDelete(storableObject.allowedUserTypes, true) {
		override def getQuery: String =
			s"""
			   |delete from ${storableObject.apexTableName} where ${storableObject.pkColumnName} = $pkSqlLiteral
			   |
      """.stripMargin
	}

	def insertIntoLocalDB(pb: PersistenceBroker): Unit =
		pb.executePreparedQueryForInsert(this.getInsertPreparedQuery)

}
