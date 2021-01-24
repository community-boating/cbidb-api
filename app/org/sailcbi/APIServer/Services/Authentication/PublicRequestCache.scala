package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services._

class PublicRequestCache(override val userName: String) extends NonMemberRequestCache(userName) {
	override def companion: RequestCacheObject[PublicRequestCache] = PublicRequestCache
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

	override def create(userName: String): PublicRequestCache = new PublicRequestCache(userName)
	def create: PublicRequestCache = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		Some(uniqueUserName)

	// Anyone can downgrade from anything to public
	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = Some(uniqueUserName)
}