package org.sailcbi.APIServer.Api.Endpoints.Staff.slacksyncusers

import play.api.libs.json.Json

case class SlackSyncResponse (
	users: List[SlackUserDto],
	userStatus: List[SlackUserStatusDto],
	messages: List[String]
)

case class SlackUserStatusDto(
	userid: String,
	dbEmail: String,
	matchesOnEmail: Int,
	banned: Boolean,
	nonActiveMember: Boolean
)

object SlackSyncResponse {
	implicit val userFormat = SlackUserDto.format
	implicit val statusFormat = Json.format[SlackUserStatusDto]
	implicit val format = Json.format[SlackSyncResponse]
}