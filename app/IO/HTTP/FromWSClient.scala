package IO.HTTP

import play.api.libs.json.JsValue
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

class FromWSClient(ws: WSClient)(implicit exec: ExecutionContext) extends HTTPMechanism {
	def getString(
		url: String,
		method: HTTPMethod,
		body: Option[Map[String, String]],
		basicAuthUsername: Option[String],
		basicAuthPassword: Option[String]
	): Future[String] = getResponse(url, method, body, basicAuthUsername, basicAuthPassword).map(res => res.body)

	def getJSON(
		url: String,
		method: HTTPMethod,
		body: Option[Map[String, String]],
		basicAuthUsername: Option[String],
		basicAuthPassword: Option[String]
	): Future[JsValue] = getResponse(url, method, body, basicAuthUsername, basicAuthPassword).map(res => res.json)

	private def getResponse(
		url: String,
		method: HTTPMethod,
		body: Option[Map[String, String]],
		basicAuthUsername: Option[String],
		basicAuthPassword: Option[String]
	): Future[WSResponse] = {
		val request: WSRequest = basicAuthUsername match {
			case None => ws.url(url)
			case Some(username) => {
				val password = basicAuthPassword match {
					case Some(s) => s;
					case None => ""
				}
				ws.url(url).withAuth(username, password, WSAuthScheme.BASIC)
			}
		}
		method match {
			case GET => request.get
			case POST => request.post(body.get)
		}
	}
}
