package Services.Authentication

case class AuthenticationInstance(
	userType: UserType,
	userName: String
)

object AuthenticationInstance {
	val PUBLIC = AuthenticationInstance(PublicUserType, PublicUserType.uniqueUserName)
	val ROOT = AuthenticationInstance(RootUserType, RootUserType.uniqueUserName)
	val APEX = AuthenticationInstance(ApexUserType, ApexUserType.uniqueUserName)
	val BOUNCER = AuthenticationInstance(BouncerUserType, ApexUserType.uniqueUserName)
}