package org.sailcbi.APIServer.Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}

case class AddJuniorClassReservationShape(
	juniorFirstName: String,
	beginnerInstanceId: Option[Int],
	intermediateInstanceId: Option[Int]
)

object AddJuniorClassReservationShape {
	implicit val format = Json.format[AddJuniorClassReservationShape]

	def apply(v: JsValue): AddJuniorClassReservationShape = v.as[AddJuniorClassReservationShape]
}
