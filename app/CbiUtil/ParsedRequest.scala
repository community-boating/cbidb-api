package CbiUtil

import play.api.mvc.{AnyContent, Cookies, Headers, Request}

case class ParsedRequest(
								headers: Headers,
								cookies: Cookies,
								path: String,
								method: String,
								remoteAddress: String,
								postParams: Map[String, String]
						) {
	def addHeader(h: (String, String)): ParsedRequest = ParsedRequest(
		headers.add(h),
		cookies,
		path,
		method,
		remoteAddress,
		postParams
	)
}

object ParsedRequest {

	object methods {
		val GET = "GET"
	}

	def apply(request: Request[AnyContent]): ParsedRequest = ParsedRequest(
		headers = request.headers,
		cookies = request.cookies,
		path = request.path,
		method = request.method,
		remoteAddress = request.remoteAddress,
		postParams = getPostParams(request)
	)

	private def getPostParams(request: Request[AnyContent]): Map[String, String] = {
		request.body.asFormUrlEncoded match {
			case None => Map.empty[String, String]
			case Some(v) =>
				v.map(Function.tupled((s: String, ss: Seq[String]) => (s, ss.mkString(""))))
		}
	}
}