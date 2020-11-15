package org.sailcbi.APIServer.Api.Endpoints.Member

import java.time.{LocalDate, LocalDateTime}

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Logic.MembershipLogic
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class GetMembershipPaymentPlansForCart @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	case class SeeTypeResult(typeId: Int, canSee: Boolean)

	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = PortalLogic.getOrderId(pb, personId)

			val now = PA.now()

			val plans = PortalLogic.getPaymentPlansForMembershipInCart(pb, personId, orderId, now.toLocalDate).map(_.map(
				payment => MembershipSinglePayment(payment._1, payment._2.cents)
			))

			implicit val format = MembershipSinglePayment.format

			Future(Ok(Json.toJson(plans)))
		})
	}

	case class MembershipSinglePayment(paymentDate: LocalDate, paymentAmountCents: Int)

	object MembershipSinglePayment{
		implicit val format = Json.format[MembershipSinglePayment]

		def apply(v: JsValue): MembershipSinglePayment = v.as[MembershipSinglePayment]
	}
}