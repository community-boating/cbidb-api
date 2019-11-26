package org.sailcbi.APIServer.CbiUtil

import io.sentry.Sentry
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Cookies, Headers, Request}

case class ParsedRequest(
	headers: Headers,
	cookies: Cookies,
	path: String,
	method: String,
	remoteAddress: String,
	postParams: Map[String, String],
	postJSON: Option[JsValue]
) {
	def addHeader(h: (String, String)): ParsedRequest = ParsedRequest(
		headers.add(h),
		cookies,
		path,
		method,
		remoteAddress,
		postParams,
		postJSON
	)
}

object ParsedRequest {

	object methods {
		val GET = "GET"
	}

	def apply(request: Request[AnyContent])(implicit PA: PermissionsAuthority): ParsedRequest = try {
		ParsedRequest(
			headers = request.headers,
			cookies = request.cookies,
			path = request.path,
			method = request.method,
			remoteAddress = request.remoteAddress,
			postParams = getPostParams(request),
			postJSON = request.body.asJson
		)
	} catch {
		case e: Throwable => {
			PA.logger.error("Failure to parse request", e)
			Sentry.capture(e)
			throw e
	}}

	private def getPostParams(request: Request[AnyContent]): Map[String, String] = {
		request.body.asFormUrlEncoded match {
			case None => Map.empty[String, String]
			case Some(v) =>
				v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))
		}
	}
}