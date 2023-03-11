package org.sailcbi.APIServer.Api.Endpoints.Staff.slacksyncusers

import com.coleji.neptune.Util.MapUtil
import play.api.libs.json.Json

case class SlackUserDto (
	username: String,
	email: String,
	status: String,
	billingActive: String,
	has2fa: String,
	hasSso: String,
	userid: String,
	fullname: String,
	displayname: String,
	expirationTimestamp: String,
)

object SlackUserDto {
	def construct(values: Map[String, String]): Either[String, SlackUserDto] = {
		val makeError = (f: String) => "Missing field: " + f
		for (
			username <- MapUtil.getOrError(values, "username", makeError);
			email <- MapUtil.getOrError(values, "email", makeError);
			status <- MapUtil.getOrError(values, "status", makeError);
			billingActive <- MapUtil.getOrError(values, "billing-active", makeError);
			has2fa <- MapUtil.getOrError(values, "has-2fa", makeError);
			hasSso <- MapUtil.getOrError(values, "has-sso", makeError);
			userid <- MapUtil.getOrError(values, "userid", makeError);
			fullname <- MapUtil.getOrError(values, "fullname", makeError);
			displayname <- MapUtil.getOrError(values, "displayname", makeError);
			expirationTimestamp <- MapUtil.getOrError(values, "expiration-timestamp", makeError)
		) yield SlackUserDto(username, email, status, billingActive, has2fa, hasSso, userid, fullname, displayname, expirationTimestamp)
	}

	implicit val format = Json.format[SlackUserDto]
}