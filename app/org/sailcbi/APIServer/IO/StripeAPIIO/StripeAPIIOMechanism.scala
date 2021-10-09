package org.sailcbi.APIServer.IO.StripeAPIIO

import com.coleji.neptune.IO.HTTP.{GET, HTTPMethod}
import com.coleji.neptune.Util.ServiceRequestResult
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
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
