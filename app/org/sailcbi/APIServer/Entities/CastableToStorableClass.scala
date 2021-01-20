package org.sailcbi.APIServer.Entities

import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForUpdateOrDelete, PreparedQueryForInsert, PreparedQueryForUpdateOrDelete, PreparedValue}
import org.sailcbi.APIServer.Services.RequestCache

trait CastableToStorableClass {
	val storableObject: CastableToStorableObject[_]
	val persistenceValues: Map[String, PreparedValue]
	val pkSqlLiteral: String

	def getInsertPreparedQuery: PreparedQueryForInsert = new PreparedQueryForInsert(storableObject.allowedUserTypes, true) {
		override val pkName: Option[String] = Some(storableObject.pkColumnName)
		val columnNamesAndValues: List[(String, PreparedValue)] = persistenceValues.toList
		val columnNames: String = columnNamesAndValues.map(_._1).mkString(", ")
		val values: List[PreparedValue] = columnNamesAndValues.map(_._2)


		override def getQuery: String =
			s"""
			   |insert into ${storableObject.apexTableName} ($columnNames) values (${values.map(_ => "?").mkString(", ")})
			   |
      """.stripMargin

		//override val params: List[String] = values
		override val preparedParams: List[PreparedValue] = values
	}

	def getUpdatePreparedQuery: PreparedQueryForUpdateOrDelete = new PreparedQueryForUpdateOrDelete(storableObject.allowedUserTypes, true) {
		val setStatements: String = persistenceValues.toList.map(t => t._1 + " = ?").mkString(", ")
		val values: List[PreparedValue] = persistenceValues.toList.map(t => t._2)


		override def getQuery: String =
			s"""
			   |update ${storableObject.apexTableName} set $setStatements where ${storableObject.pkColumnName} = $pkSqlLiteral
			   |
      """.stripMargin

		override val preparedParams: List[PreparedValue] = values
	}

	def getDeletePreparedQuery: HardcodedQueryForUpdateOrDelete = new HardcodedQueryForUpdateOrDelete(storableObject.allowedUserTypes, true) {
		override def getQuery: String =
			s"""
			   |delete from ${storableObject.apexTableName} where ${storableObject.pkColumnName} = $pkSqlLiteral
			   |
      """.stripMargin
	}

	def insertIntoLocalDB(rc: RequestCache[_]): Unit =
		rc.executePreparedQueryForInsert(this.getInsertPreparedQuery)

}
