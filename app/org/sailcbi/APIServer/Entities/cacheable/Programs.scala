package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.ProgramType

import java.time.Duration

object Programs extends CacheableFactory[Null, IndexedSeq[ProgramType]] {
	override protected val lifetime: Duration = Duration.ofMinutes(5)

	override protected def calculateKey(config: Null): String = CacheKeys.programTypes

	override protected def generateResult(rc: RequestCache, config: Null): IndexedSeq[ProgramType] = {
		getObjects(rc.assertUnlocked).toIndexedSeq
	}

	private def getObjects(rc: UnlockedRequestCache)(implicit PA: PermissionsAuthority): IndexedSeq[ProgramType] = {
		rc.getAllObjectsOfClass(ProgramType, Set(
			ProgramType.fields.programId,
			ProgramType.fields.programName
		)).toIndexedSeq
	}
}
