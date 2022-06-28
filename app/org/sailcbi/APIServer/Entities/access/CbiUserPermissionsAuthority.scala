package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.{Permission, Role, UserPermissionsAuthority}
import com.coleji.neptune.Core.{CacheableFactory, RequestCache, UnlockedRequestCache}
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.{User, UserRole}
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys

import java.time.Duration

class CbiUserPermissionsAuthority(
	override val userId: Int,
	override val userName: String,
	override val roles: Set[Role],
	override val permissions: Set[Permission]
) extends UserPermissionsAuthority[Int](userId, userName, roles, permissions) with Serializable

object CbiUserPermissionsAuthority extends CacheableFactory[String, CbiUserPermissionsAuthority] {
	override protected val lifetime: Duration = Duration.ofMinutes(5)

	override protected def calculateKey(config: String): String = CacheKeys.userPermissionsAuthority(config)

	override protected def serialize(result: CbiUserPermissionsAuthority): String = {
		val userId = Serde.serializeStandard(result.userId)
		val userName = Serde.serializeStandard(result.userName)
		val roles = Serde.serializeStandard(result.roles)
		val permissions = Serde.serializeStandard(result.permissions)
		userId + ":" + userName + ":" + roles + ":" + permissions
	}

	override protected def deseralize(resultString: String): CbiUserPermissionsAuthority = {
		val parts = resultString.split(":")
		val userId = Serde.deseralizeStandard[Int](parts(0))
		val userName = Serde.deseralizeStandard[String](parts(1))
		val roles = Serde.deseralizeStandard[Set[Role]](parts(2))
		val permissions = Serde.deseralizeStandard[Set[Permission]](parts(3))
		new CbiUserPermissionsAuthority(userId, userName, roles, permissions)
	}

	override protected def generateResult(rc: RequestCache, config: String): CbiUserPermissionsAuthority = rc match {
		case urc: UnlockedRequestCache => apply(urc, config)
		case _ => throw new Exception("Unable to generate a UPA from a locked RC")
	}

	private def apply(rc: UnlockedRequestCache, userName: String): CbiUserPermissionsAuthority = {
		val userId = {
			val users = rc.getObjectsByFilters(User, List(User.fields.userName.alias.equalsConstantLowercase(userName.toLowerCase)))
			if (users.length != 1) throw new Exception ("Found " + users.length + " users for username " + userName)
			users.head.values.userId.get
		}

		val roles: List[Role] = rc.getObjectsByFilters(UserRole, List(UserRole.fields.userId.alias.equalsConstant(userId)))
			.map(_.values.roleId.get)
			.map(CbiAccess.allRoles(_))

		val permissions = roles.map(_.permissions).foldLeft(Set.empty.asInstanceOf[Set[Permission]])(_ ++ _)

		new CbiUserPermissionsAuthority(
			userId,
			userName,
			roles.toSet,
			permissions
		)
	}
}
