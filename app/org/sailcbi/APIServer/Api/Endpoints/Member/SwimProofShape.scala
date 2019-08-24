package org.sailcbi.APIServer.Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}

case class SwimProofShape(
	personId: Int,
	swimProofId: Option[Int]
)

object SwimProofShape {
	implicit val format = Json.format[SwimProofShape]

	def apply(v: JsValue): SwimProofShape = v.as[SwimProofShape]
}
