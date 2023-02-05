package org.sailcbi.APIServer.Logic.IO

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.FlagChange

private[Logic] object DockhouseIo {
	def putFlagChange(rc: UnlockedRequestCache, flagColor: String): FlagChange = {
		val fc = new FlagChange
		fc.values.flag.update(flagColor)
		fc.values.changeDatetime.update(rc.PA.now())
		rc.commitObjectToDatabase(fc)
		fc
	}
}
