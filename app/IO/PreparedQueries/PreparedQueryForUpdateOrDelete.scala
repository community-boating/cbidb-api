package IO.PreparedQueries

import Services.Authentication.UserType

abstract class PreparedQueryForUpdateOrDelete(
  override val allowedUserTypes: Set[UserType],
  override val useTempSchema: Boolean = false
) extends PreparedQuery(allowedUserTypes, useTempSchema) {

}
