package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.{Permission, Role, UserPermissionsAuthority}
import com.coleji.neptune.Core.{CacheableFactory, RequestCache, UnlockedRequestCache}
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.{AccessProfileRole, User, UserRole}
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys

import java.time.Duration

class CbiUserPermissionsAuthority(
	override val userId: Int,
	override val userName: String,
	override val roles: Set[Role],
	override val permissions: Set[Permission]
) extends UserPermissionsAuthority[Int](userId, userName, roles, permissions) with Serializable {
	override def toString: String = roles.toString() + " ::: " + permissions.toString()
}

object CbiUserPermissionsAuthority extends CacheableFactory[String, CbiUserPermissionsAuthority] {
	override protected val lifetime: Duration = Duration.ofSeconds(5)

	override protected def calculateKey(config: String): String = CacheKeys.userPermissionsAuthority(config)

	override protected def serialize(result: CbiUserPermissionsAuthority): String = {
		val userId = Serde.serializeStandard(result.userId)
		val userName = Serde.serializeStandard(result.userName)
		val roles = Serde.serializeList(result.roles.toList.map(_.id))
		val permissions = Serde.serializeStandard(result.permissions)
		userId + "%" + userName + "%" + roles + "%" + permissions
	}

	override protected def deseralize(resultString: String): CbiUserPermissionsAuthority = {
		val parts = resultString.split("%")
		val userId = Serde.deseralizeStandard[Int](parts(0))
		val userName = Serde.deseralizeStandard[String](parts(1))
		val roles = Serde.deserializeList[Int](parts(2)).map(id => CbiAccessUtil.roleMap(id)).toSet
		val permissions = Serde.deseralizeStandard[Set[Permission]](parts(3))
		new CbiUserPermissionsAuthority(userId, userName, roles, permissions)
	}

	override protected def generateResult(rc: RequestCache, config: String): CbiUserPermissionsAuthority = rc match {
		case urc: UnlockedRequestCache => apply(urc, config)
		case _ => throw new Exception("Unable to generate a UPA from a locked RC")
	}

	private def apply(rc: UnlockedRequestCache, userName: String): CbiUserPermissionsAuthority = {
		val user = {
			val users = rc.getObjectsByFilters(
				User,
				List(User.fields.userName.alias.equalsConstantLowercase(userName.toLowerCase)),
				Set(
					User.fields.userId,
					User.fields.userName,
					User.fields.accessProfileId,
				)
			)
			if (users.length != 1) throw new Exception ("Found " + users.length + " users for username " + userName)
			users.head
		}

		val userId = user.values.userId.get
		val accessProfileId = user.values.accessProfileId.get

		val apRoles = rc.getObjectsByFilters(
			AccessProfileRole,
			List(AccessProfileRole.fields.accessProfileId.alias.equalsConstant(accessProfileId)),
			Set(
				AccessProfileRole.fields.accessProfileId,
				AccessProfileRole.fields.roleId
			)
		)
			.map(_.values.roleId.get)
			.map(CbiAccessUtil.roleMap(_))
			.toSet

		val overrideRoles = rc.getObjectsByFilters(
			UserRole,
			List(UserRole.fields.userId.alias.equalsConstant(userId)),
			Set(
				UserRole.fields.userId,
				UserRole.fields.roleId
			)
		)
			.map(_.values.roleId.get)
			.map(CbiAccessUtil.roleMap(_))
			.toSet

		val roles = apRoles ++ overrideRoles

		val permissions = roles.map(_.permissions).foldLeft(Set.empty.asInstanceOf[Set[Permission]])(_ ++ _)

		new CbiUserPermissionsAuthority(
			userId,
			userName,
			roles,
			permissions
		)
	}
}
