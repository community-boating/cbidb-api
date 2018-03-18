package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import CbiUtil._
import Entities.JsFacades.Stripe.{Charge, StripeError, Token}
import IO.HTTP.{GET, HTTPMechanism, POST}
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import Services.{PermissionsAuthority, ServerStateContainer}
import play.api.libs.json.{JsArray, JsObject, JsValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class StripeAPIIOLiveService(baseURL: String, secretKey: String, http: HTTPMechanism)(implicit exec: ExecutionContext) extends StripeAPIIOMechanism {
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

  def createCharge(dbIO: StripeDatabaseIOMechanism, amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] = {
    def makeRequest(): Future[JsValue] = {
      http.getJSON(
        baseURL + "charges",
        POST,
        Some(Map(
          "amount" -> amountInCents.toString,
          "currency" -> "usd",
          "source" -> token,
          "description" -> ("Charge for orderId " + orderId + " time " + ServerStateContainer.get.nowDateTimeString),
          "metadata[closeId]" -> closeId.toString,
          "metadata[orderId]" -> orderId.toString,
          "metadata[token]" -> token,
          "metadata[cbiInstance]" -> PermissionsAuthority.instanceName.get
        )),
        Some(secretKey),
        Some("")
      )
    }

    val f1: Future[Failover[JsValue, ServiceRequestResult[Charge, StripeError]]] = {
      makeRequest().transform({
        case Success(jsv: JsValue) => Success(Resolved(jsv))
        case Failure(e: Throwable) => Success(Failed(e))
      })
    }

    val f2: Future[Failover[Charge, ServiceRequestResult[Charge, StripeError]]] = f1.map(_.andThen(
      jsVal => Charge(jsVal),
      jsVal => ValidationError(StripeError(jsVal))
    ))

    f2.map({
      case Resolved(c: Charge) => {
        dbIO.createCharge(c)
        Succeeded(c)
      }
      case Rejected(se: ServiceRequestResult[Charge, StripeError]) => se
      case Failed(e: Throwable) => CriticalError(e)
    })
  }

  def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]] = {
    def makeRequest(): Future[JsValue] = {
      http.getJSON(
        baseURL + "tokens/" + token,
        GET,
        None,
        Some(secretKey),
        Some("")
      )
    }

    val f1: Future[Failover[JsValue, ServiceRequestResult[Token, StripeError]]] = {
      makeRequest().transform({
        case Success(jsv: JsValue) => Success(Resolved(jsv))
        case Failure(e: Throwable) => Success(Failed(e))
      })
    }

    val f2: Future[Failover[Token, ServiceRequestResult[Token, StripeError]]] = f1.map(_.andThen(
      jsVal => Token(jsVal),
      jsVal => ValidationError(StripeError(jsVal))
    ))


    f2.map({
      case Resolved(t: Token) => Succeeded(t)
      case Rejected(se: ServiceRequestResult[Token, StripeError]) => se
      case Failed(e: Throwable) => CriticalError(e)
    })
  }
}
