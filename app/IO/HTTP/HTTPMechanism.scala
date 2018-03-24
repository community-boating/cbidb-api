package IO.HTTP

import play.api.libs.json.JsValue

import scala.concurrent.Future

abstract class HTTPMechanism {
  def getString(
    url: String,
    method: HTTPMethod,
    body: Option[Map[String, String]],
    basicAuthUsername: Option[String],
    basicAuthPassword: Option[String]
  ): Future[String]

  def getJSON(
    url: String,
    method: HTTPMethod,
    body: Option[Map[String, String]],
    basicAuthUsername: Option[String],
    basicAuthPassword: Option[String]
  ): Future[JsValue]
}
