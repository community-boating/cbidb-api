package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import IO.HTTP.{GET, HTTPMechanism}
import Stripe.JsFacades.Charge
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.{ExecutionContext, Future}

class StripeAPIIOLiveService(baseURL: String, secretKey: String, http: HTTPMechanism)(implicit exec: ExecutionContext) extends {
  def getCharges(since: Option[ZonedDateTime], chargesPerRequest: Int = 100): Future[List[Charge]] = {
    def makeRequest(url: String, params: List[String], lastID: Option[String], results: List[Charge]): Future[List[Charge]] = {
      val finalParams: String = (lastID match {
        case None => params
        case Some(id) => ("starting_after=" + id) :: params
      }).mkString("&")

      val finalURL = if(finalParams.length == 0) url else url + "?" + finalParams

    http.getJSON(finalURL, GET, None, Some(secretKey), Some("")).flatMap(res => {
      println(res)
      val json = res.as[JsObject].value("data").as[JsArray].value
      json.map(e => Charge(e)).toList match {
        case Nil => Future {results}
        case l => makeRequest(url, params, Some(l.last.id), results ++ l)
      }
    })
  }

  val params = ("limit=" + chargesPerRequest) :: (since match {
      case None => List.empty
      case Some(d) => List("created[gte]=" + d.toEpochSecond)
    })
    makeRequest(baseURL + "charges", params, None, List.empty)
  }
}
