package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import com.coleji.framework.Util.PropertiesWrapper
import com.redis.RedisClientPool
import org.sailcbi.APIServer.Entities.EntityDefinitions.User

class BouncerRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool)
extends LockedRequestCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[BouncerRequestCache] = BouncerRequestCache

	def getUserByUsername(username: String): Option[User] =
	pb.getObjectsByFilters(User, List(User.fields.userName.equalsConstantLowercase(username.toLowerCase))) match {
		case u :: Nil => Some(u)
		case _ => None
	}

	def updateUser(user: User): Unit = pb.commitObjectToDatabase(user)
}

object BouncerRequestCache extends RequestCacheObject[BouncerRequestCache] {
	val uniqueUserName = "BOUNCER"

	val BOUNCER_AUTH_HEADER = "origin-bouncer"

	override val requireCORSPass: Boolean = false

	def getPwHashForUser(parsedRequest: ParsedRequest, userName: String, userType: RequestCacheObject[_])(implicit PA: PermissionsAuthority): Option[(String, String, String)] = {
		PA.withRequestCacheNoFuture(BouncerRequestCache)(None, parsedRequest, rc => {
			if (PA.systemParams.allowableUserTypes.contains(userType)) {
				userType match {
					case MemberRequestCache => MemberRequestCache.getPwHashForUser(rc, userName)
					case StaffRequestCache => StaffRequestCache.getPwHashForUser(rc, userName)
					case _ => None
				}
			} else None
		}).flatten
	}

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): BouncerRequestCache =
		new BouncerRequestCache(userName, serverParams, dbGateway, redisPool)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): BouncerRequestCache = create(uniqueUserName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] =
		if (
			request.headers.get(BOUNCER_AUTH_HEADER).contains("true") &&
					PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None
}
