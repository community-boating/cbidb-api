package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.JpClassSession
import org.sailcbi.APIServer.IO.CachedData

object JpClassLogic {
	// TODO: not loving this
	def setWeekAlias(session: JpClassSession, rc: UnlockedRequestCache) = {
		val cache = new CachedData(rc)
		cache.getJpWeekAlias(session.values.sessionDatetime.get.toLocalDate)
	}
}
