package Chargify

import java.util.Properties

import CbiUtil.PropertiesWrapper
import Entities.EntityDefinitions.Person
import Services.RequestCache
import play.api.libs.json.{JsObject, JsString, JsValue}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

object Request {
  def getSubscriptions(rc: RequestCache, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] =
    get(rc, "subscriptions", ws)

  def getCustomers(rc: RequestCache, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] =
    get(rc, "customers", ws)

  def getPaymentProfiles(rc: RequestCache, ws: WSClient) (implicit exec: ExecutionContext): Future[JsObject] =
    get(rc, "payment_profiles", ws)

  /**
    *
    * @param p    Person to create in chargify
    * @param rc   RequestCache
    * @param ws   WSClient
    * @param exec Execution context for chargify request
    * @return     Chargify id# of customer corresponding to person
    */
  def createCustomer(p: Person, rc: RequestCache, ws: WSClient) (implicit exec: ExecutionContext): Future[Int] = {
    val body: JsObject = JsObject(Map(
      "customer" -> JsObject(Map(
        "first_name" -> JsString(p.values.nameFirst.get.get),
        "last_name" -> JsString(p.values.nameLast.get.get),
        "email" -> JsString(p.values.email.get.get),
        "reference" -> JsString(p.values.personId.get.toString)
      ))
    ))
    post(rc, "customers", ws, body).map(v => {
      v("data")("customer")("id").toString().toInt
    })
  }

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

  private def post(rc: RequestCache, endpoint: String, ws: WSClient, body: JsValue) (implicit exec: ExecutionContext): Future[JsObject] = {
    val request: WSRequest =
      ws.url("https://" + getChargifyProps(rc).subdomain + ".chargify.com/" + endpoint + ".json")
        .withAuth(getChargifyProps(rc).apiKey, "X", WSAuthScheme.BASIC)
        .addHttpHeaders("Content-Type" -> "application/json")
    val futureResponse: Future[WSResponse] = request.post(body)
    val jsonFuture: Future[JsObject] = futureResponse.map(_.json match {
      case v: JsValue => new JsObject(Map("data" -> v))
      case x =>  {
        println("Responsse: " + x)
        throw new Exception("Received unparsable json response")
      }
    })
    jsonFuture
  }
}
