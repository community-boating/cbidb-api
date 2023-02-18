package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.BoatType
import play.api.libs.json.Json

import java.time.Duration

object BoatTypes extends CacheableFactory[Null, IndexedSeq[BoatType]] {
	override protected val lifetime: Duration = Duration.ofMinutes(5)

	override protected def calculateKey(config: Null): String = CacheKeys.boatTypes

	override protected def generateResult(rc: RequestCache, config: Null): IndexedSeq[BoatType] = {
		getObjects(rc.assertUnlocked).toIndexedSeq
	}

	private def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): IndexedSeq[BoatType] = {
		rc.getAllObjectsOfClass(BoatType, Set(
			BoatType.fields.boatId,
			BoatType.fields.boatName,
			BoatType.fields.active,
			BoatType.fields.displayOrder,
			BoatType.fields.minCrew,
			BoatType.fields.maxCrew,
		)).toIndexedSeq
	}
}
