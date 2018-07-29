package Entities

import IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForUpdateOrDelete}
import Services.Authentication.ApexUserType
import Services.PersistenceBroker

trait CastableToStorableClass {
  val storableObject: CastableToStorableObject[_]
  val persistenceValues: Map[String, String]
  val pkSqlLiteral: String

  def getInsertPreparedQuery: HardcodedQueryForInsert = new HardcodedQueryForInsert(Set(ApexUserType), true) {
    override val pkName: Option[String] = Some(storableObject.pkColumnName)
    val columnNamesAndValues: List[(String, String)] = persistenceValues.toList
    val columnNames: String = columnNamesAndValues.map(_._1).mkString(", ")
    val values: String = columnNamesAndValues.map(_._2).mkString(", ")
    override def getQuery: String =
      s"""
         |insert into $storableObject.apexTableName ($columnNames) values ($values)
         |
      """.stripMargin
  }

  def getUpdatePreparedQuery: HardcodedQueryForUpdateOrDelete = new HardcodedQueryForUpdateOrDelete(Set(ApexUserType), true) {
    val setStatements: String = persistenceValues.toList.map(t => t._1 + " = " + t._2).mkString(", ")
    override def getQuery: String =
      s"""
         |update $storableObject.apexTableName set $setStatements where $storableObject.pkColumnName = $pkSqlLiteral
         |
      """.stripMargin
  }

  def getDeletePreparedQuery: HardcodedQueryForUpdateOrDelete = new HardcodedQueryForUpdateOrDelete(Set(ApexUserType), true) {
    override def getQuery: String =
      s"""
         |delete from $storableObject.apexTableName where $storableObject.pkColumnName = $pkSqlLiteral
         |
      """.stripMargin
  }

  def insertIntoLocalDB(pb: PersistenceBroker): Unit =
    pb.executePreparedQueryForInsert(this.getInsertPreparedQuery)

}
