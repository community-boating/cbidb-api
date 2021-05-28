package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.framework.API
import com.coleji.framework.API.{ResultError, ValidationOk}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.framework.Util.{CriticalError, NetSuccess, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.StripeError
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{LockedRequestCacheWithStripeController, MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class StorePaymentMethod @Inject()(implicit exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def postAP()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, StorePaymentMethodShape.apply)(parsed => {
				post(rc, parsed.paymentMethodId, parsed.retryLatePayments, rc.getAuthedPersonId, None)
			})
		})
	}
	def postJP(juniorId: Option[Int])(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val parsedRequest = ParsedRequest(req)
		juniorId match {
			case None => PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
				PA.withParsedPostBodyJSON(parsedRequest.postJSON, StorePaymentMethodShape.apply)(parsed => {
					post(rc, parsed.paymentMethodId, parsed.retryLatePayments, rc.getAuthedPersonId, None)
				})
			})
			case Some(id) => MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, id, rc => {
				PA.withParsedPostBodyJSON(parsedRequest.postJSON, StorePaymentMethodShape.apply)(parsed => {
					post(rc, parsed.paymentMethodId, parsed.retryLatePayments, rc.getAuthedPersonId, juniorId)
				})
			})
		}
	}

	def postAutoDonate()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, StorePaymentMethodShape.apply)(parsed => {
				post(rc, parsed.paymentMethodId, false, rc.getAuthedPersonId, None)
			})
		})
	}

	def postDonate()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, StorePaymentMethodShape.apply)(parsed => {
				post(rc, parsed.paymentMethodId, false, rc.getAuthedPersonId.get, None)
			})
		})
	}

	private def post(rc: LockedRequestCacheWithStripeController, paymentMethodId: String, retryLatePayments: Boolean, adultPersonId: Int, juniorId: Option[Int])(implicit PA: PermissionsAuthority): Future[Result] =  {
		val stripe = rc.getStripeIOController(ws)
		val memberPersonId = juniorId.getOrElse(adultPersonId)
		val customerIdOption = PortalLogic.getStripeCustomerId(rc, adultPersonId)
		customerIdOption match {
			case None => Future(Ok("fail"))
			case Some(customerId) => {
				stripe.storePaymentMethod(customerId, paymentMethodId).flatMap({
					case ve: ValidationError[_, StripeError] => {
						if (ve.errorObject.`type` == "card_error") {
							Future(Ok(ResultError("process_err", ve.errorObject.message).asJsObject()))
						} else {
							throw new Exception("non-card stripe error: " + ve.errorObject)
						}
					}
					case e: CriticalError[_, StripeError] => throw e.e
					case _: NetSuccess[_, StripeError] => PortalLogic.updateAllPaymentIntentsWithNewMethod(rc, adultPersonId, paymentMethodId, stripe).map(_ => {
						// TODO: this is a list of SRRs; we should confirm they all worked
						println("called update all intents with new method")
						val orderId = PortalLogic.getOpenStaggeredOrderForPerson(rc, memberPersonId)
						if (retryLatePayments && orderId.isDefined) {
							println("... and let's retry failed payments")
							PortalLogic.retryFailedPayments(rc, adultPersonId, orderId.get) match {
								case ValidationOk => Ok(JsObject(Map("success" -> JsBoolean(true))))
								case ve: API.ValidationError => Ok(ve.toResultError.asJsObject())
							}
						} else {
							Ok(JsObject(Map("success" -> JsBoolean(true))))
						}
					})
				})
			}
		}
	}

	case class StorePaymentMethodShape(paymentMethodId: String, retryLatePayments: Boolean)

	object StorePaymentMethodShape {
		implicit val format = Json.format[StorePaymentMethodShape]

		def apply(v: JsValue): StorePaymentMethodShape = v.as[StorePaymentMethodShape]
	}
}
