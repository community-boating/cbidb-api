package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Exception.UserTypeMismatchException
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.{Filter, StorableClass, StorableObject}

class PublicRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends LockedRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[PublicRequestCache] = PublicRequestCache

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

	/*
	def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = obj match {
	  case ApClassFormat => EntityVisibility(entityVisible=true, None, Some(Set(
		ApClassFormat.fields.typeId
	  )))
	  case ApClassInstance => EntityVisibility(entityVisible=true, None, Some(Set(
		ApClassInstance.fields.formatId,
		ApClassInstance.fields.locationString
	  )))
	  case ApClassSession => EntityVisibility(entityVisible=true, None, Some(Set(
		ApClassSession.fields.instanceId,
		ApClassSession.fields.sessionDateTime
	  )))
	  case ApClassSignup => EntityVisibility(entityVisible=true, None, Some(Set(
		ApClassSignup.fields.instanceId
	  )))
	  case ApClassType => EntityVisibility(entityVisible=true, None, Some(Set(
		ApClassType.fields.displayOrder,
		ApClassType.fields.typeName
	  )))
	  case ClassInstructor => EntityVisibility(entityVisible=true, None, Some(Set(
		ClassInstructor.fields.nameFirst,
		ClassInstructor.fields.nameLast
	  )))
	  case ClassLocation => EntityVisibility(entityVisible=true, None, Some(Set(
		ClassLocation.fields.locationName
	  )))
	  case JpClassInstance => EntityVisibility(entityVisible=true, None, Some(Set(
		JpClassInstance.fields.locationId,
		JpClassInstance.fields.typeId,
		JpClassInstance.fields.instructorId
	  )))
	  case JpClassSession => EntityVisibility(entityVisible=true, None, Some(Set(
		JpClassSession.fields.instanceId,
		JpClassSession.fields.sessionDateTime
	  )))
	  case JpClassSignup => EntityVisibility(entityVisible=true, None, Some(Set(
		JpClassSignup.fields.instanceId
	  )))
	  case JpClassType => EntityVisibility(entityVisible=true, None, Some(Set(
		JpClassType.fields.displayOrder,
		JpClassType.fields.typeName
	  )))
	  case JpTeam => EntityVisibility(entityVisible=true, None, Some(Set(
		JpTeam.fields.teamName
	  )))
	  case JpTeamEventPoints => EntityVisibility(entityVisible=true, None, Some(Set(
		JpTeamEventPoints.fields.teamId,
		JpTeamEventPoints.fields.points
	  )))
	  case _ => EntityVisibility.ZERO_VISIBILITY
	}
	*/
}

object PublicRequestCache extends RequestCacheObject[PublicRequestCache] {
	val uniqueUserName = "PUBLIC"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): PublicRequestCache = new PublicRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): PublicRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		Some(uniqueUserName)

	// Anyone can downgrade from anything to public
//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = Some(uniqueUserName)
}