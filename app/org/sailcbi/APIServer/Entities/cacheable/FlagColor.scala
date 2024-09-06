package org.sailcbi.APIServer.Entities.cacheable

import com.coleji.neptune.Core.{CacheableFactory, PermissionsAuthority, RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.IO.PreparedQueries.Public.GetFlagColor

import java.time.Duration

object FlagColor  extends CacheableFactory[Null, String] {
	override protected val lifetime: Duration = Duration.ofSeconds(5)

	override protected def calculateKey(config: Null): String = CacheKeys.flagColor

	override protected def generateResult(rc: RequestCache, config: Null): String = {
		println("CALCULATING FLAG")
		rc.executePreparedQueryForSelect(new GetFlagColor).map(_.color).head
	}
}
