package IO.PreparedQueries

import Services.Authentication.UserType

abstract class PreparedQuery(
  val allowedUserTypes: Set[UserType],
  val useTempSchema: Boolean = false
) {
  def getQuery: String
}
