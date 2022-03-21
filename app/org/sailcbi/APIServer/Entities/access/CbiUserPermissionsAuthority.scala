package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.{Permission, Role, UserPermissionsAuthority}
import com.coleji.neptune.Core.{CacheableFactory, RequestCache, UnlockedRequestCache}
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys

import java.time.Duration

class CbiUserPermissionsAuthority(
	override val user: String,
	override val roles: Set[Role],
	override val permissions: Set[Permission]
) extends UserPermissionsAuthority[String](user, roles, permissions) with Serializable

object CbiUserPermissionsAuthority extends CacheableFactory[String, CbiUserPermissionsAuthority] {
	override protected val lifetime: Duration = Duration.ofMinutes(5)

	override protected def calculateKey(config: String): String = CacheKeys.userPermissionsAuthority(config)

	override protected def serialize(result: CbiUserPermissionsAuthority): String = {
		val user = Serde.serializeStandard(result.user)
		val roles = Serde.serializeStandard(result.roles)
		val permissions = Serde.serializeStandard(result.permissions)
		user + ":" + roles + ":" + permissions
	}

	override protected def deseralize(resultString: String): CbiUserPermissionsAuthority = {
		val parts = resultString.split(":")
		val user = Serde.deseralizeStandard[String](parts(0))
		val roles = Serde.deseralizeStandard[Set[Role]](parts(1))
		val permissions = Serde.deseralizeStandard[Set[Permission]](parts(2))
		new CbiUserPermissionsAuthority(user, roles, permissions)
	}

	override protected def generateResult(rc: RequestCache, config: String): CbiUserPermissionsAuthority = rc match {
		case urc: UnlockedRequestCache => apply(urc, config)
		case _ => throw new Exception("Unable to generate a UPA from a locked RC")
	}

	def apply(rc: UnlockedRequestCache, userName: String): CbiUserPermissionsAuthority = {
		val admins = Set(
			"JCOLE",
			"KLIOLIOS", "AALLETAG")
		val isAdmin = admins.contains(userName.toUpperCase)
		new CbiUserPermissionsAuthority(
			userName,
			Set.empty,
			if (isAdmin) Set(CbiAccess.permissions.PERM_GENERAL_ADMIN) else Set.empty
		)
	}
}
