package IO.PreparedQueries

import Services.Authentication.UserType

abstract class PreparedQueryForInsert(
  override val allowedUserTypes: Set[UserType],
  override val useTempSchema: Boolean = false
) extends PreparedQuery(allowedUserTypes, useTempSchema) {
  val pkName: Option[String]
}
