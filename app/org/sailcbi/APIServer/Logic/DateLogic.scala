package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Core.PermissionsAuthority

import java.time.{LocalDate, LocalDateTime, Month}

object DateLogic {
	implicit val PA: PermissionsAuthority = PermissionsAuthority.PA
	def now: LocalDateTime = LocalDateTime.now.plusSeconds(PA.systemParams.serverTimeOffsetSeconds)

	def currentSeason(asOf: LocalDate = now.toLocalDate): Int = {
		val currentYear = asOf.getYear
		asOf.getMonth match {
			case Month.NOVEMBER | Month.DECEMBER => currentYear + 1
			case _ => currentYear
		}
	}
}
