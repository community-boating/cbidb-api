package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Exception.UserTypeMismatchException
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.{Filter, StorableClass, StorableObject}

class KioskRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends NonMemberRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[KioskRequestCache] = KioskRequestCache

	override def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] =
		throw new UserTypeMismatchException()

	override def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] =
		throw new UserTypeMismatchException()

	override def countObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int = {
		throw new UserTypeMismatchException()
	}

	override def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T] =
		throw new UserTypeMismatchException()

	override def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] =
		throw new UserTypeMismatchException()

	override def commitObjectToDatabase(i: StorableClass): Unit =
		throw new UserTypeMismatchException()
}

object KioskRequestCache extends RequestCacheObject[KioskRequestCache] {
	val uniqueUserName = "KIOSK"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): KioskRequestCache = new KioskRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): KioskRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		if (request.headers.get("Am-CBI-Kiosk").contains(kioskToken)) Some(uniqueUserName)
		else None

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = None
}