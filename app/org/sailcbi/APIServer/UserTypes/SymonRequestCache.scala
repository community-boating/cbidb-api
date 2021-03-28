package org.sailcbi.APIServer.UserTypes

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Exception.UserTypeMismatchException
import org.sailcbi.APIServer.Services._
import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.StorableObject

class SymonRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends LockedRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[SymonRequestCache] = SymonRequestCache
}

object SymonRequestCache extends RequestCacheObject[SymonRequestCache] {
	val uniqueUserName = "SYMON"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): SymonRequestCache = new SymonRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): SymonRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		try {
			println("here we go")
			val host: String = request.postParams("symon-host")
			println("symon host is " + host)
			val program = request.postParams("symon-program")
			val argString = request.postParams("symon-argString")
			val status = request.postParams("symon-status").toInt
			val mac = request.postParams("symon-mac")
			val candidateHash = request.postParams("symon-hash")
			println("All args were present")
			val isValid = PA.validateSymonHash(
				host = host,
				program = program,
				argString = argString,
				status = status,
				mac = mac,
				candidateHash = candidateHash
			)
			if (isValid) Some(uniqueUserName)
			else None
		} catch {
			case e: Throwable => {
				println(e)
				None
			}
		}
	}

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}