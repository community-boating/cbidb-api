package CbiUtil

import Services.Authentication.{RootUserType, UserType}

object TestUserType {
  def apply(allowed: Set[UserType], test: UserType): Boolean =
    test == RootUserType || allowed.contains(test)
}
