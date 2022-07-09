package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.AccessProfile
import play.api.libs.json.Json

import java.time.Duration

object AccessProfiles extends CacheableFactory[Null, String] {
	override protected val lifetime: Duration = Duration.ofMinutes(5)

	override protected def calculateKey(config: Null): String = CacheKeys.accessProfiles

	override protected def generateResult(rc: RequestCache, config: Null): String = {
		Json.toJson(getObjects(rc.assertUnlocked)).toString()
	}

	def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): List[AccessProfile] = {
		rc.getAllObjectsOfClass(AccessProfile, Set(
			AccessProfile.fields.accessProfileId,
			AccessProfile.fields.name,
			AccessProfile.fields.description,
			AccessProfile.fields.displayOrder
		))
	}
}
