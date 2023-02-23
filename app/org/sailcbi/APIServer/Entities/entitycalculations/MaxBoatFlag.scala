package org.sailcbi.APIServer.Entities.entitycalculations

import play.api.libs.json.Json

case class MaxBoatFlag(
	boatId: Int,
	programId: Int,
	maxFlag: String
)

object MaxBoatFlag {
	implicit val format = Json.format[MaxBoatFlag]
}