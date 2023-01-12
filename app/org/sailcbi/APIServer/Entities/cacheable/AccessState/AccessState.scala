package org.sailcbi.APIServer.Entities.cacheable.AccessState

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.access.CbiAccessUtil
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys
import play.api.libs.json.Json

import java.time.Duration

object AccessState extends CacheableFactory[Null, String]{
	override protected val lifetime: Duration = Duration.ofSeconds(5)

	override protected def calculateKey(config: Null): String = CacheKeys.accessState

	override protected def generateResult(rc: RequestCache, config: Null): String = {
		val accessProfilesRelationships = rc.assertUnlocked.getAllObjectsOfClass(AccessProfileRelationship, Set(
			AccessProfileRelationship.fields.managingProfileId,
			AccessProfileRelationship.fields.subordinateProfileId
		))

		val users = rc.assertUnlocked.getObjectsByFilters(User, List(User.fields.active.alias.equals(true)), Set(
			User.fields.userId,
			User.fields.userName,
			User.fields.accessProfileId
		), 200)

		val usersRoles = rc.assertUnlocked.getObjectsByFilters(
			UserRole,
			List(UserRole.fields.userId.alias.inList(users.map(_.values.userId.get))),
			Set(
				UserRole.fields.userId,
				UserRole.fields.roleId
			)
		)
		users.foreach(u => u.references.extraRoles.set(usersRoles.filter(_.values.userId.get == u.values.userId.get)))

		val accessProfileRoles = rc.assertUnlocked.getAllObjectsOfClass(AccessProfileRole, Set(
			AccessProfileRole.fields.assignId,
			AccessProfileRole.fields.accessProfileId,
			AccessProfileRole.fields.roleId,
		))

		val accessProfiles = rc.assertUnlocked.getAllObjectsOfClass(AccessProfile, Set(
			AccessProfile.fields.accessProfileId,
			AccessProfile.fields.name,
			AccessProfile.fields.description,
			AccessProfile.fields.displayOrder,
		)).sortBy(_.values.displayOrder.get)

		val roles = CbiAccessUtil.allRoles

		val ret = AccessState(
			accessProfiles = accessProfiles.map(ap => AccessProfileDisplay(
				id = ap.values.accessProfileId.get,
				name = ap.values.name.get,
				roles = accessProfileRoles.filter(_.values.accessProfileId.get == ap.values.accessProfileId.get).map(_.values.roleId.get))
			),
			roles = roles.map(r => RoleDisplay(id = r.id, name = r.name, description = r.description, permissions = r.permissions.map(_.id))),
			users = users.map(u => UserDisplay(
				userName = u.values.userName.get,
				accessProfileId = u.values.accessProfileId.get,
				extraRoles = u.references.extraRoles.get.map(_.values.roleId.get)
			)),
			accessProfileRelationships = accessProfilesRelationships.map(r => AccessProfileRelationshipDisplay(
				managingProfileId = r.values.managingProfileId.get,
				subordinateProfileId = r.values.subordinateProfileId.get
			))
		)

		implicit val writeRoleDisplay = Json.writes[RoleDisplay]
		implicit val writeAccessProfileDisplay = Json.writes[AccessProfileDisplay]
		implicit val writeUserDisplay = Json.writes[UserDisplay]
		implicit val writeAccessProfileRelationshipDisplay = Json.writes[AccessProfileRelationshipDisplay]
		implicit val writeAccessState = Json.writes[AccessState]

		Json.toJson(ret).toString()
	}

	case class RoleDisplay(id: Int, name: String, description: String, permissions: List[Int])
	case class AccessProfileDisplay(id: Int, name: String, roles: List[Int])
	case class UserDisplay(userName: String, accessProfileId: Int, extraRoles: List[Int])
	case class AccessProfileRelationshipDisplay(managingProfileId: Int, subordinateProfileId: Int)
	case class AccessState(
		accessProfiles: List[AccessProfileDisplay],
		roles: List[RoleDisplay],
		users: List[UserDisplay],
		accessProfileRelationships: List[AccessProfileRelationshipDisplay]
	)
}
