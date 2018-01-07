package Chargify

import java.util.Properties

import CbiUtil.PropertiesWrapper
import Services.RequestCache
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

object Request {
  def getSubscriptions(rc: RequestCache, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] =
    get(rc, "subscriptions", ws)

  def getCustomers(rc: RequestCache, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] =
    get(rc, "customers", ws)

  def createCustomer(rc: RequestCache, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] = post(rc, "customers", ws)

  private def getChargifyProps(rc: RequestCache) = rc.getChargifyConfig.get

  private def get(rc: RequestCache, endpoint: String, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] = {
    val request: WSRequest =
      ws.url("https://" + getChargifyProps(rc).subdomain + ".chargify.com/" + endpoint + ".json")
        .withAuth(getChargifyProps(rc).apiKey, "X", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = request.get()
    val jsonFuture: Future[JsObject] = futureResponse.map(_.json match {
      case v: JsValue => new JsObject(Map("data" -> v))
      case _ =>  throw new Exception("Received unparsable json response")
    })
    jsonFuture
  }

  private def post(rc: RequestCache, endpoint: String, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] = {
    val request: WSRequest =
      ws.url("https://" + getChargifyProps(rc).subdomain + ".chargify.com/" + endpoint + ".json")
        .withAuth(getChargifyProps(rc).apiKey, "X", WSAuthScheme.BASIC)
        .addHttpHeaders("Content-Type" -> "application/json")
    val futureResponse: Future[WSResponse] = request.post("{\"customer\": {\"first_name\": \"Ted\"}}")
    val jsonFuture: Future[JsObject] = futureResponse.map(_ match {
      case v: JsValue => new JsObject(Map("data" -> v))
      case x =>  {
        println("Responsse: " + x + x.body)
        throw new Exception("Received unparsable json response")
      }
    })
    jsonFuture
  }
}
