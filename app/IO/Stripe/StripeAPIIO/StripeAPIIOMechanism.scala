package IO.Stripe.StripeAPIIO

import CbiUtil.ServiceRequestResult
import Entities.JsFacades.Stripe.StripeError
import IO.HTTP.{GET, HTTPMethod}
import play.api.libs.json.JsValue

import scala.concurrent.Future

abstract class StripeAPIIOMechanism {
	def getOrPostStripeSingleton[T](
		url: String,
		constructor: JsValue => T,
		httpMethod: HTTPMethod = GET,
		body: Option[Map[String, String]] = None,
		postSuccessAction: Option[T => _] = None
	): Future[ServiceRequestResult[T, StripeError]]

	def getStripeList[T](
		url: String,
		constructor: JsValue => T,
		getID: T => String,
		params: List[String],
		fetchSize: Int
	): Future[ServiceRequestResult[List[T], StripeError]]
}
