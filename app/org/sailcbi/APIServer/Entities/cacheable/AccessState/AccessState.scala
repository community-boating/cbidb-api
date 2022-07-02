package org.sailcbi.APIServer.Entities.cacheable.AccessState

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import com.coleji.neptune.Core.access.{AccessProfile, Permission, Role}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{AccessProfileRole, User, UserRole}
import org.sailcbi.APIServer.Entities.access.CbiAccessUtil
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys
import play.api.libs.json.Json

import java.time.Duration

object AccessState extends CacheableFactory[Null, String]{
	override protected val lifetime: Duration = Duration.ofSeconds(5)

	override protected def calculateKey(config: Null): String = CacheKeys.accessState

	override protected def generateResult(rc: RequestCache, config: Null): String = {
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

		val accessProfileRoles = rc.assertUnlocked.getAllObjectsOfClass(AccessProfileRole)

		val accessProfiles = CbiAccessUtil.allAccessProfiles
		val roles = CbiAccessUtil.allRoles

		val ret = AccessState(
			accessProfiles = accessProfiles.map(ap => AccessProfileDisplay(id = ap.id, name = ap.name, roles = accessProfileRoles.map(_.values.roleId.get))),
			roles = roles.map(r => RoleDisplay(id = r.id, name = r.name, description = r.description, permissions = r.permissions.map(_.id))),
			users = users.map(u => UserDisplay(
				userName = u.values.userName.get,
				accessProfileId = u.values.accessProfileId.get,
				extraRoles = u.references.extraRoles.get.map(_.values.roleId.get)
			))
		)

		implicit val writeRoleDisplay = Json.writes[RoleDisplay]
		implicit val writeAccessProfileDisplay = Json.writes[AccessProfileDisplay]
		implicit val writeUserDisplay = Json.writes[UserDisplay]
		implicit val writeAccessState = Json.writes[AccessState]

		Json.toJson(ret).toString()
	}

	case class RoleDisplay(id: Int, name: String, description: String, permissions: List[Int])
	case class AccessProfileDisplay(id: Int, name: String, roles: List[Int])
	case class UserDisplay(userName: String, accessProfileId: Int, extraRoles: List[Int])
	case class AccessState(accessProfiles: List[AccessProfileDisplay], roles: List[RoleDisplay], users: List[UserDisplay])
}
