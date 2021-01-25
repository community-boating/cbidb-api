package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Customer, StripeError}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetRecurringDonationInfo @Inject()(implicit exec: ExecutionContext, ws: WSClient) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId(rc)

			PortalLogic.getStripeCustomerId(rc, personId) match {
				case None => Future(Ok("Error"))
				case Some(customerId) => {
					rc.getStripeIOController(ws).getCustomerObject(customerId).map({
						case f: NetFailure[Customer, StripeError] => {
							f match {
								case ce: CriticalError[_, _] => {
									ce.e.printStackTrace
								}
								case ve: ValidationError[_, _] => {
									println(ve.errorObject)
								}
							}
							Ok("Error")
						}
						case s: NetSuccess[Customer, StripeError] => {
							val defaultPaymentId = s.successObject.invoice_settings.default_payment_method
							val response = GetRecurringDonationInfoShape(defaultPaymentId)
							implicit val format = GetRecurringDonationInfoShape.format
							Ok(Json.toJson(response))
						}
					})
				}
			}
		})
	}

	case class GetRecurringDonationInfoShape(
													defaultPaymentId: Option[String]
											)

	object GetRecurringDonationInfoShape {
		implicit val format = Json.format[GetRecurringDonationInfoShape]

		def apply(v: JsValue): GetRecurringDonationInfoShape = v.as[GetRecurringDonationInfoShape]
	}

}