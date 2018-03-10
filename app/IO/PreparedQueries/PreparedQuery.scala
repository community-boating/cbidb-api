package IO.PreparedQueries

import java.sql.ResultSet

import Services.Authentication.UserType

abstract class PreparedQuery[T] {
  // TODO: implement user type downgrading on these
  val allowedUserTypes: Set[UserType]
  def getQuery: String
  def mapResultSetRowToCaseObject(rs: ResultSet): T
}
