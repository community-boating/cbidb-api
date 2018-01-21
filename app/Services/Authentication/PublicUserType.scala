package Services.Authentication

import Entities.EntityDefinitions._
import Services._
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{AnyContent, Request}

object PublicUserType extends UserType {
  val publicUserName = "PUBLIC"
  def getAuthenticatedUsernameInRequest(request: Request[AnyContent], rootCB: CacheBroker, apexToken: String): Option[String] = Some(publicUserName)

  // Anyone can downgrade from anything to public
  def getAuthenticatedUsernameFromSuperiorAuth(
    request: Request[AnyContent],
    rc: RequestCache,
    desiredUserName: String
  ): Option[String] = Some(publicUserName)

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

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
}
