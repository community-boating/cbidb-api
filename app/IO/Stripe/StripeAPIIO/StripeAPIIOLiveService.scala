package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import CbiUtil._
import Entities.JsFacades.Stripe.{BalanceTransaction, Charge, StripeError, Token}
import IO.HTTP.{GET, HTTPMechanism, HTTPMethod, POST}
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import Services.{PermissionsAuthority, ServerStateContainer}
import play.api.libs.json.{JsArray, JsObject, JsValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

// TODO: separate the core methods from the things like getCharges, getTokenDetails that are just concrete implementations of the core methods
// Maybe the instance things should go in a companion, which is parameterized with a API Mechanism object?
// The only things in this class should be core talking-to-stripe functionality
class StripeAPIIOLiveService(baseURL: String, secretKey: String, http: HTTPMechanism)(implicit exec: ExecutionContext) extends StripeAPIIOMechanism {
  def getCharges(since: Option[ZonedDateTime], chargesPerRequest: Int = 100): Future[List[Charge]] =
    getStripeList[Charge](
      baseURL + "charges",
      Charge.apply,
      (c: Charge) => c.id,
      since match {
        case None => List.empty
        case Some(d) => List("created[gte]=" + d.toEpochSecond)
      },
      chargesPerRequest
    ).map({
      case Succeeded(t) => t
      case Warning(t, _) => t
      case _ => throw new Exception("failed to get charges")
    })

  def createCharge(dbIO: StripeDatabaseIOMechanism, amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] =
    getOrPostStripeSingleton(
      baseURL + "charges",
      Charge.apply,
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
      Some((c: Charge) => dbIO.createObject(c))
    )

  // TODO: adds no value
  def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]] =
    getOrPostStripeSingleton(baseURL + "tokens/" + token, Token.apply, GET, None, None)

  // TODO: replace with call to method parameterized by BalanceTransaction
  def getBalanceTransactions: Future[ServiceRequestResult[List[BalanceTransaction], StripeError]] =
    getStripeList(
      "balance/history",
      BalanceTransaction.apply,
      (bt: BalanceTransaction) => bt.id,
      List.empty,
      100
    )

  private def getOrPostStripeSingleton[T](
    url: String,
    constructor: JsValue => T,
    httpMethod: HTTPMethod = GET,
    body: Option[Map[String, String]] = None,
    postSuccessAction: Option[T => _] = None
  ): Future[ServiceRequestResult[T, StripeError]] = {
    def makeRequest(): Future[JsValue] = {
      http.getJSON(
        url,
        httpMethod,
        body,
        Some(secretKey),
        Some("")
      )
    }

    val f1: Future[Failover[JsValue, ServiceRequestResult[T, StripeError]]] = {
      makeRequest().transform({
        case Success(jsv: JsValue) => Success(Resolved(jsv))
        case Failure(e: Throwable) => Success(Failed(e))
      })
    }

    val f2: Future[Failover[T, ServiceRequestResult[T, StripeError]]] = f1.map(_.andThenWithFailover(
      jsVal => constructor(jsVal),
      jsVal => {
        try {
          ValidationError(StripeError(jsVal))
        } catch {
          case e: Throwable => throw new Exception("Could not parse stripe response as success or error obj:\n" + jsVal.toString() + "\noriginal exception: " + e.getMessage)
        }
      }
    ))

    type DoThing = T => _

    f2.map({
      case Resolved(t: T) => postSuccessAction match {
        case None => Succeeded(t)
        case Some(a: DoThing) => try {
          a(t)
          Succeeded(t)
        } catch {
          case e: Throwable => Warning(t, e)
        }
      }
      case Rejected(se: ServiceRequestResult[T, StripeError]) => se
      case Failed(e: Throwable) => CriticalError(e)
    })
  }

  def getStripeList[T](
    url: String,
    constructor: JsValue => T,
    getID: T => String,
    params: List[String],
    fetchSize: Int
  ): Future[ServiceRequestResult[List[T], StripeError]] = {
    def makeRequest(eachUrl: String, params: List[String], lastID: Option[String], results: List[T]): Future[ServiceRequestResult[List[T], StripeError]] = {
      println("entered makeRequest with " + results.length)
      val finalParams: String = (lastID match {
        case None => params
        case Some(id) => ("starting_after=" + id) :: params
      }).mkString("&")

      val finalURL = if(finalParams.length == 0) eachUrl else eachUrl + "?" + finalParams
      println("making request to " + finalURL)

      val f1: Future[Failover[JsValue, ServiceRequestResult[List[T], StripeError]]] =
        http.getJSON(finalURL, GET, None, Some(secretKey), Some("")).transform({
          case Success(jsv: JsValue) => {
            println(jsv.toString())
            Success(Resolved(jsv))
          }
          case Failure(e: Throwable) => Success(Failed(e))
        })

      val f2: Future[Failover[List[JsValue], ServiceRequestResult[List[T], StripeError]]] =  f1.map(_.andThenWithFailover(
        jsVal => jsVal.as[JsObject].value("data").as[JsArray].value.toList,
        jsVal => try {
          ValidationError(StripeError(jsVal))
        } catch {
          case e: Throwable => throw new Exception("Could not parse stripe response as success or error obj:\n" + jsVal.toString() + "\noriginal exception: " + e.getMessage)
        }
      ))

      val f3: Future[Failover[List[T], ServiceRequestResult[List[T], StripeError]]] = f2.map(_.andThen(
        (jsvs: List[JsValue]) => jsvs.map(constructor)
      ))

      f3.flatMap({
        case Resolved(l: List[T]) => l match {
          case Nil => {
            println("actually its " + l.length)
            Future { Succeeded(results) }
          }
          case more => {
            println("started with " + results.length + ", adding " + more.length)
            makeRequest(eachUrl, params, Some(getID(more.last)), results ++ more)
          }
          case _ => {
            println("shoudlnt be here")
            Future { Succeeded(results) }
          }
        }
        case Rejected(se: ServiceRequestResult[T, StripeError]) => Future { se }
        case Failed(e) => Future { CriticalError(e) }
      })
    }

    makeRequest(baseURL + url, ("limit=" + fetchSize) :: params, None, List.empty)
  }
}
