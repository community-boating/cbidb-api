package IO.PreparedQueries

import Services.Authentication.UserType

abstract class PreparedQueryForSelect[T](
												override val allowedUserTypes: Set[UserType],
												override val useTempSchema: Boolean = false
										) extends HardcodedQueryForSelect[T](allowedUserTypes, useTempSchema) {
	val params: List[String]
}
