package org.sailcbi.APIServer.Api.Endpoints.Public

import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent, Controller, Cookie}

class CheckProtoPersonCookie extends Controller {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action { request => {
		val cookieName = "CBIDB_PROTO"
		val hasCookie = request.cookies.toSet.map((c: Cookie) => c.name).contains(cookieName)
		if (hasCookie) {
			Ok("Detected existing cookie")
		} else {
			val headers = request.headers
			val secure = headers.toSimpleMap.get("Outside-Connection-HTTPS").map(v => v != "false").getOrElse(true)
			val cookie = Cookie(
				name = cookieName,
				value = scala.util.Random.alphanumeric.take(40).mkString,
				maxAge = Some(60 * 60 * 24), // 24hr
				secure = secure,
				httpOnly = true
			)
			Ok("Setting new cookie").withCookies(cookie)
		}

	}}
}