package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core.{CacheBroker, LockedRequestCache, PermissionsAuthority, RequestCacheObject}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Services.Exception.UserTypeMismatchException
import org.sailcbi.APIServer.Services._
import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.StorableObject

class BouncerRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends LockedRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[BouncerRequestCache] = BouncerRequestCache

	def getUserByUsername(username: String): Option[User] = pb.getObjectsByFilters(User, List(User.fields.userName.equalsConstantLowercase(username.toLowerCase))) match {
		case u :: Nil => Some(u)
		case _ => None
	}

	def updateUser(user: User): Unit = pb.commitObjectToDatabase(user)
}

object BouncerRequestCache extends RequestCacheObject[BouncerRequestCache] {
	val uniqueUserName = "BOUNCER"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): BouncerRequestCache = new BouncerRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): BouncerRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		apexToken: String,
		kioskToken: String
	)(implicit PA: PermissionsAuthority): Option[String] =
		if (
			request.headers.get(PermissionsAuthority.BOUNCER_AUTH_HEADER).contains("true") &&
					PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}