package org.sailcbi.APIServer.Entities.dto.docusign

import play.api.libs.json.{JsValue, Json}

case class AccessTokenDto(
	access_token: String,
	token_type: String,
	expires_in: Int,
	scope: String
)

object AccessTokenDto {
	implicit val format = Json.format[AccessTokenDto]

	def apply(v: JsValue): AccessTokenDto = v.as[AccessTokenDto]
}