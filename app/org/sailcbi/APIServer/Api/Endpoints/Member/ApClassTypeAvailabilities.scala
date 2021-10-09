package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{CacheBroker, ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.Portal.PortalLogic.ApClassAvailability
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassTypeAvailabilities @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val cb: CacheBroker = rc.cb
			val personId = rc.getAuthedPersonId

			val types = PortalLogic.getApClassTypeAvailabilities(rc, personId)
			val voucherCt = PortalLogic.getApAvailableVoucherCount(rc, personId)
			implicit val format = ApClassTypeAvailabilitiesShape.format
			Future(Ok(Json.toJson(ApClassTypeAvailabilitiesShape(
				types = types,
				voucherCt = voucherCt
			))))
		})
	}

	case class ApClassTypeAvailabilitiesShape(
		types: List[ApClassAvailability],
		voucherCt: Int
	)

	object ApClassTypeAvailabilitiesShape {
		implicit val format = Json.format[ApClassTypeAvailabilitiesShape]
		def apply(v: JsValue): ApClassTypeAvailabilitiesShape = v.as[ApClassTypeAvailabilitiesShape]
	}
}
