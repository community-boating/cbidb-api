package org.sailcbi.APIServer.Logic.DockhouseLogic

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassSignup, FlagChange}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Logic.IO.DockhouseIo

object DockhouseLogic {
	def putFlagChange(rc: UnlockedRequestCache, flagColor: String): Either[String, FlagChange] = {
		val ok = flagColor match {
			case MagicIds.FLAG_COLORS.FLAG_GREEN => true
			case MagicIds.FLAG_COLORS.FLAG_YELLOW => true
			case MagicIds.FLAG_COLORS.FLAG_RED => true
			case _ => false
		}

		if (ok) {
			Right(DockhouseIo.putFlagChange(rc, flagColor))
		} else {
			Left("Unrecognized flag color " + flagColor)
		}
	}

	def addPersonToApClass(rc: UnlockedRequestCache, personId: Int, instanceId: Int): Either[String, ApClassSignup] = {
		// TODO: for DH, confirm the instance has a session today
		DockhouseIo.addPersonToApClass(rc, personId, instanceId)
	}
}
