package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{BoatType, Signout}
import play.api.libs.json.Json

import java.time.Duration

object BoatTypes extends CacheableFactory[Null, String] {
	override protected val lifetime: Duration = Duration.ofMinutes(1)

	override protected def calculateKey(config: Null): String = CacheKeys.boatTypes

	override protected def generateResult(rc: RequestCache, config: Null): String = {
		Json.toJson(getObjects(rc.assertUnlocked)).toString()
	}

	def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): List[BoatType] = {
		rc.getAllObjectsOfClass(BoatType)
	}
}
