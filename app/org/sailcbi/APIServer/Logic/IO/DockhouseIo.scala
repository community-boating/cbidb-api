package org.sailcbi.APIServer.Logic.IO

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassSignup, FlagChange}

private[Logic] object DockhouseIo {
	def putFlagChange(rc: UnlockedRequestCache, flagColor: String): FlagChange = {
		val fc = new FlagChange
		fc.values.flag.update(flagColor)
		fc.values.changeDatetime.update(rc.PA.now())
		rc.commitObjectToDatabase(fc)
		fc
	}

	def addPersonToApClass(rc: UnlockedRequestCache, personId: Int, instanceId: Int): Either[String, ApClassSignup] = null
}
