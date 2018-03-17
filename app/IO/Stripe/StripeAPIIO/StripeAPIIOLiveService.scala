package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import CbiUtil._
import Entities.JsFacades.Stripe.{Charge, StripeError, Token}
import IO.HTTP.{GET, HTTPMechanism, POST}
import Services.{PermissionsAuthority, ServerStateContainer}
import play.api.libs.json.{JsArray, JsObject, JsValue}

import scala.concurrent.{ExecutionContext, Future}

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

  def createCharge(amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] = {
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

    val f1: Failover[Future[JsValue], Future[ServiceRequestResult[Charge, StripeError]]] = Failover(makeRequest())
      .andCatch(e => Future { new CriticalError(e)})

    val f2 = f1
      .andThen(
        jsValFut => jsValFut.map(jsVal => Charge(jsVal)),
        jsValFut => jsValFut.map(jsVal => new ValidationError(StripeError(jsVal)))
      )

    f2 match {
      case Resolved(c: Charge) => Future{new Succeeded(c)}
      case Rejected(se: StripeError) => Future{ new ValidationError(se)}
      case Failed(e: Throwable) => Future { new CriticalError(e)}
    }
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

    val f1: Failover[Future[JsValue], Future[ServiceRequestResult[Token, StripeError]]] = Failover(makeRequest())
      .andCatch(e => Future { new CriticalError(e)})

    val f2 = f1
      .andThen(
        jsValFut => jsValFut.map(jsVal => Token(jsVal)),
        jsValFut => jsValFut.map(jsVal => new ValidationError(StripeError(jsVal)))
      )

    f2 match {
      case Resolved(t: Token) => Future{new Succeeded(t)}
      case Rejected(se: StripeError) => Future{ new ValidationError(se)}
      case Failed(e: Throwable) => Future { new CriticalError(e)}
    }
  }
}
