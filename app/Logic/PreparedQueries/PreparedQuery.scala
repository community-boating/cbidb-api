package Logic.PreparedQueries

import java.sql.ResultSet

import Api.ApiDataObject
import Services.Authentication.UserType

abstract class PreparedQuery[T <: ApiDataObject] {
  val allowedUserTypes: Set[UserType]
  def getQuery: String
  def mapResultSetRowToCaseObject(rs: ResultSet): T
}
