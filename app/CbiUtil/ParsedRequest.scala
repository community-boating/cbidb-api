package CbiUtil

import play.api.mvc.{AnyContent, Cookies, Headers, Request}

case class ParsedRequest(
  headers: Headers,
  cookies: Cookies,
  postParams: Map[String, String]
)

object ParsedRequest {
  def apply(request: Request[AnyContent]): ParsedRequest = ParsedRequest(
    headers = request.headers,
    cookies = request.cookies,
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