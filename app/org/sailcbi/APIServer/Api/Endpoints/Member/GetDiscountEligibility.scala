package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class GetDiscountEligibility @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val (youth, senior, veteran, renew) = PortalLogic.getAPDiscountEligibilities(pb, personId)

			val result = GetDiscountEligibilityResult(
				youth = youth,
				senior = senior,
				veteran = veteran,
				renew = renew
			)

			implicit val format = GetDiscountEligibilityResult.format
			Future(Ok(Json.toJson(result)))
		})
	}

	case class GetDiscountEligibilityResult(
		youth: Boolean,
		senior: Boolean,
		veteran: Boolean,
		renew: Boolean
	)

	object GetDiscountEligibilityResult {
		implicit val format = Json.format[GetDiscountEligibilityResult]

		def apply(v: JsValue): GetDiscountEligibilityResult = v.as[GetDiscountEligibilityResult]
	}
}