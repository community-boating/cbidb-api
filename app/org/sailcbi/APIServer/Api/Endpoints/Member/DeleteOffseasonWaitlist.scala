package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeleteOffseasonWaitlist @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, DeleteOffseasonWaitlistShape.apply)(parsed => {
			PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId, rc => {
				val pb = rc.pb
				println(parsed)

				PortalLogic.removeOffseasonWaitlist(rc, parsed.juniorId)
				Future(Ok(new JsObject(Map("success" -> JsBoolean(true)))))
			})
		})
	}

	case class DeleteOffseasonWaitlistShape (
		juniorId: Int
	)

	object DeleteOffseasonWaitlistShape {
		implicit val format = Json.format[DeleteOffseasonWaitlistShape]

		def apply(v: JsValue): DeleteOffseasonWaitlistShape = v.as[DeleteOffseasonWaitlistShape]
	}
}
