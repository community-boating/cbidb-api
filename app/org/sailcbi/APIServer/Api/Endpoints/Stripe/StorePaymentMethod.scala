package org.sailcbi.APIServer.Api.Endpoints.Stripe

import javax.inject.Inject
import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.{CriticalError, NetFailure, NetSuccess, ParsedRequest, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.{MemberUserType, PublicUserType}
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class StorePaymentMethod @Inject()(implicit exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, StorePaymentMethodShape.apply)(parsed => {
				val pb = rc.pb
				val stripe = rc.getStripeIOController(ws)
				val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
				val orderId = PortalLogic.getOrderId(pb, personId)
				val totalInCents = PortalLogic.getOrderTotalCents(pb, orderId)
				val customerIdOption = PortalLogic.getStripeCustomerId(pb, personId)
				customerIdOption match {
					case None => Future(Ok("fail"))
					case Some(customerId) => {
						stripe.storePaymentMethod(customerId, parsed.paymentMethodId).flatMap({
							case ve: ValidationError[_, StripeError] => {
								if (ve.errorObject.`type` == "card_error") {
									Future(Ok(ResultError("process_err", ve.errorObject.message).asJsObject()))
								} else {
									throw new Exception("non-card stripe error: " + ve.errorObject)
								}
							}
							case e: CriticalError[_, StripeError] => throw e.e
							case _: NetSuccess[_, StripeError] => PortalLogic.getOrCreatePaymentIntent(pb, stripe, personId, orderId, totalInCents).flatMap(pi => {
								stripe.updatePaymentIntentWithPaymentMethod(pi.id, parsed.paymentMethodId).map({
									case ve: ValidationError[_, StripeError] =>  {
										println(ve.errorObject)
										Ok(JsObject(Map("success" -> JsBoolean(true))))
									}
									case s: NetSuccess[_, _] => Ok(JsObject(Map("success" -> JsBoolean(true))))
								})
							})
						})
					}
				}
			})
		})
	}

	case class StorePaymentMethodShape(paymentMethodId: String)

	object StorePaymentMethodShape {
		implicit val format = Json.format[StorePaymentMethodShape]

		def apply(v: JsValue): StorePaymentMethodShape = v.as[StorePaymentMethodShape]
	}
}
