package org.sailcbi.APIServer.Services.StripeAPIIO

import org.sailcbi.APIServer.CbiUtil.ServiceRequestResult
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import com.coleji.framework.IO.HTTP.{GET, HTTPMethod}
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
