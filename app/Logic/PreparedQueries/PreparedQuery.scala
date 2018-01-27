package Logic.PreparedQueries

import java.sql.ResultSet

import Services.Authentication.UserType

abstract class PreparedQuery[T <: PreparedQueryCaseResult] {
  val allowedUserTypes: Set[UserType]
  def getQuery: String
  def mapResultSetRowToCaseObject(rs: ResultSet): T
}
