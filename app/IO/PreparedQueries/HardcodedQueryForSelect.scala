package IO.PreparedQueries

import java.sql.ResultSet

import Services.Authentication.UserType
import Services.ResultSetWithOption

abstract class HardcodedQueryForSelect[T](
  override val allowedUserTypes: Set[UserType],
  override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
  implicit def wrapResultSet(rs: ResultSet): ResultSetWithOption = ResultSetWithOption.wrapResultSet(rs)
  def mapResultSetRowToCaseObject(rs: ResultSet): T
}