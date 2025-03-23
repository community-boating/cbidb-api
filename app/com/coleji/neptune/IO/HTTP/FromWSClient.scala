package com.coleji.neptune.IO.HTTP

import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class HTTPError(status: Int) extends Throwable ("HTTP Error, Status Code: " + status)

class FromWSClient(ws: WSClient)(implicit exec: ExecutionContext) extends HTTPMechanism {
	def getString(
		url: String,
		method: HTTPMethod,
		body: Option[Map[String, String]],
		basicAuthUsername: Option[String],
		basicAuthPassword: Option[String],
		bearerToken: Option[String]
	): Future[String] = getResponse(url, method, body, basicAuthUsername, basicAuthPassword, bearerToken).map(res => res.body)

	def getJSON(
		url: String,
		method: HTTPMethod,
		body: Option[Map[String, String]],
		basicAuthUsername: Option[String],
		basicAuthPassword: Option[String],
		bearerToken: Option[String]
	): Future[JsValue] = getResponse(url, method, body, basicAuthUsername, basicAuthPassword, bearerToken).map(res => res.json)

	private def getResponse(
		url: String,
		method: HTTPMethod,
		body: Option[Map[String, String]],
		basicAuthUsername: Option[String],
		basicAuthPassword: Option[String],
		bearerToken: Option[String],
	): Future[WSResponse] = {
		var request: WSRequest = basicAuthUsername match {
			case None => ws.url(url)
			case Some(username) => {
				val password = basicAuthPassword.getOrElse("")
				ws.url(url).withAuth(username, password, WSAuthScheme.BASIC)
			}
		}
		request = bearerToken match {
			case None => request
			case Some(t) => request.withHttpHeaders(("Authorization", "Bearer " + t))
		}
		(method match {
			case GET => request.get
			case POST => request.post(body.getOrElse(Map.empty))
		}).transform({
			case Success(s) => if (s.status == OK) Success(s) else Failure(HTTPError(s.status))
			case Failure(f) => Failure(f)
		})
	}
}
