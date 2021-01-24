package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetMembershipPaymentPlansForCart @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	case class SeeTypeResult(typeId: Int, canSee: Boolean)

	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb = rc.pb

			val personId = rc.auth.getAuthedPersonId(rc)
			val orderId = PortalLogic.getOrderIdAP(rc, personId)

			val now = PA.now()

			val plansResult = PortalLogic.getPaymentPlansForMembershipInCart(rc, personId, orderId, now.toLocalDate)

			val scm = plansResult._1.get

			val plans = plansResult._2.map(_.map(
				payment => MembershipSinglePayment(payment._1, payment._2.cents)
			))

			implicit val format = MembershipSinglePayment.format

			Future(Ok(new JsObject(Map(
				"scm" -> Json.toJson(scm),
				"plans" -> Json.toJson(plans)
			))))
		})
	}

	case class MembershipSinglePayment(paymentDate: LocalDate, paymentAmtCents: Int)

	object MembershipSinglePayment{
		implicit val format = Json.format[MembershipSinglePayment]

		def apply(v: JsValue): MembershipSinglePayment = v.as[MembershipSinglePayment]
	}
}