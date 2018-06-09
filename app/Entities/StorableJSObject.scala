package Entities

import IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForUpdateOrDelete}
import Services.Authentication.ApexUserType

abstract class StorableJSObject {
  val apexTableName: String
  val persistenceFields: Map[String, String]
  val pkColumnName: String
  val pkSqlLiteral: String

  def getInsertPreparedQuery: HardcodedQueryForInsert = new HardcodedQueryForInsert(Set(ApexUserType), true) {
    override val pkName: Option[String] = Some(pkColumnName)
    val columnNamesAndValues: List[(String, String)] = persistenceFields.toList
    val columnNames: String = columnNamesAndValues.map(_._1).mkString(", ")
    val values: String = columnNamesAndValues.map(_._2).mkString(", ")
    override def getQuery: String =
      s"""
         |insert into $apexTableName ($columnNames) values ($values)
         |
      """.stripMargin
  }

  def getUpdatePreparedQuery: HardcodedQueryForUpdateOrDelete = new HardcodedQueryForUpdateOrDelete(Set(ApexUserType), true) {
    val setStatements: String = persistenceFields.toList.map(t => t._1 + " = " + t._2).mkString(", ")
    override def getQuery: String =
      s"""
         |update $apexTableName set $setStatements where $pkColumnName = $pkSqlLiteral
         |
      """.stripMargin
  }

  def getDeletePreparedQuery: HardcodedQueryForUpdateOrDelete = new HardcodedQueryForUpdateOrDelete(Set(ApexUserType), true) {
    override def getQuery: String =
      s"""
         |delete from $apexTableName where $pkColumnName = $pkSqlLiteral
         |
      """.stripMargin
  }
}
