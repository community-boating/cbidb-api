package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core.{ParsedRequest, _}
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.PropertiesWrapper
import org.sailcbi.APIServer.Server.CBIBootLoaderLive

class ApexRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway)
extends LockedRequestCacheWithStripeController(userName, serverParams, dbGateway) {
	override def companion: RequestCacheObject[ApexRequestCache] = ApexRequestCache
}

object ApexRequestCache extends RequestCacheObject[ApexRequestCache] {
	val uniqueUserName = "APEX"

	override val requireCORSPass: Boolean = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): ApexRequestCache =
		new ApexRequestCache(userName, serverParams, dbGateway)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): ApexRequestCache = create(uniqueUserName, serverParams, dbGateway)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] = {
		val headers = request.headers.toMap
		val headerKey = "apex-token"
		if (
			headers.contains(headerKey) &&
			headers(headerKey).mkString("") == customParams.getString(CBIBootLoaderLive.PROPERTY_NAMES.APEX_TOKEN)
		) Some(uniqueUserName)
		else None
	}

	def validateApexSessionKey(rc: RequestCache, userName: String, apexSession: String, apexSessionKey: String): Boolean = {
		val q = new PreparedQueryForSelect[Int](Set(BouncerRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = 1

			override val params: List[String] = List(userName.toLowerCase(), apexSession, apexSessionKey)

			override def getQuery: String = "select 1 from session_keys where lower(username) = ? and apex_session = ? and remote_key = ?"
		}
		rc.executePreparedQueryForSelect(q).length == 1
	}
}
