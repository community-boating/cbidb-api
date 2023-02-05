package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.FlagChange
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
}
