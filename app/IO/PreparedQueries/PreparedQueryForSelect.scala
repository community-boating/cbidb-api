package IO.PreparedQueries

import java.sql.ResultSet

import Services.Authentication.UserType

abstract class PreparedQueryForSelect[T](
  override val allowedUserTypes: Set[UserType],
  override val useTempSchema: Boolean = false
) extends PreparedQuery(allowedUserTypes, useTempSchema) {
  def mapResultSetRowToCaseObject(rs: ResultSet): T
}
