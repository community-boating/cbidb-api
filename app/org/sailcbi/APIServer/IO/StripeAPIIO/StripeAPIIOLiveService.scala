package org.sailcbi.APIServer.IO.StripeAPIIO

import com.coleji.neptune.IO.HTTP.{GET, HTTPMechanism, HTTPMethod}
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import play.api.libs.json.{JsArray, JsObject, JsValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class StripeAPIIOLiveService (baseURL: String, secretKey: String, http: HTTPMechanism)(implicit exec: ExecutionContext) extends StripeAPIIOMechanism {
	def getOrPostStripeSingleton[T](
		url: String,
		constructor: JsValue => T,
		httpMethod: HTTPMethod = GET,
		body: Option[Map[String, String]] = None,
		postSuccessAction: Option[T => _] = None
   ): Future[ServiceRequestResult[T, StripeError]] = {
		def makeRequest(): Future[JsValue] = {
			println("Making " + httpMethod.toString + " stripe call to " + url)
			body match {
				case Some(b) => println(b)
				case _ =>
			}
			http.getJSON(
				baseURL + url,
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

			val finalURL = if (finalParams.length == 0) eachUrl else eachUrl + "?" + finalParams
			println("making request to " + finalURL)

			val f1: Future[Failover[JsValue, ServiceRequestResult[List[T], StripeError]]] =
				http.getJSON(finalURL, GET, None, Some(secretKey), Some("")).transform({
					case Success(jsv: JsValue) => {
						//  println(jsv.toString())
						Success(Resolved(jsv))
					}
					case Failure(e: Throwable) => Success(Failed(e))
				})

			val f2: Future[Failover[List[JsValue], ServiceRequestResult[List[T], StripeError]]] = f1.map(_.andThenWithFailover(
				jsVal => jsVal.as[JsObject].value("data").as[JsArray].value.toList,
				jsVal => try {
					ValidationError(StripeError(jsVal))
				} catch {
					case e: Throwable => throw new Exception("Could not parse stripe response as success or error obj:\n" + jsVal.toString() + "\noriginal exception: " + e.getMessage)
				}
			))

			val f3: Future[Failover[List[T], ServiceRequestResult[List[T], StripeError]]] = f2.map(_.andThen(
				(jsvs: List[JsValue]) => jsvs.map(jsv => {
					//println("about to construct " + jsv)
					constructor(jsv)
				})
			))

			f3.flatMap({
				case Resolved(l: List[T]) => l match {
					case Nil => {
						println("actually its " + l.length)
						Future {
							Succeeded(results)
						}
					}
					case more => {
						println("started with " + results.length + ", adding " + more.length)
						makeRequest(eachUrl, params, Some(getID(more.last)), results ++ more)
					}
					case _ => {
						println("shoudlnt be here")
						Future {
							Succeeded(results)
						}
					}
				}
				case Rejected(se: ServiceRequestResult[T, StripeError]) => Future {
					se
				}
				case Failed(e) => Future {
					CriticalError(e)
				}
			})
		}

		makeRequest(baseURL + url, ("limit=" + fetchSize) :: params, None, List.empty)
	}
}
