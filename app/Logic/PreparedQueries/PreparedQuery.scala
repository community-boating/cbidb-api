package Logic.PreparedQueries

import java.sql.ResultSet

import Api.ApiDataObject
import Services.Authentication.UserType

abstract class PreparedQuery[T <: ApiDataObject] {
  // TODO: implement user type downgrading on these
  val allowedUserTypes: Set[UserType]
  def getQuery: String
  def mapResultSetRowToCaseObject(rs: ResultSet): T
}
