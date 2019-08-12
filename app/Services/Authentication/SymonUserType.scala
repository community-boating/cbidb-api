package Services.Authentication

import CbiUtil.ParsedRequest
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}

object SymonUserType extends NonMemberUserType {
	val uniqueUserName = "SYMON"

	def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String): Option[String] = {
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
			val isValid = PermissionsAuthority.validateSymonHash(
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

	def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: AuthenticationInstance,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

	def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

	def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
