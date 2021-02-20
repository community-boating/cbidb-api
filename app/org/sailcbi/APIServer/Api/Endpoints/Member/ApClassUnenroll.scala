package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApClassUnenroll  @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(parsedRequest, rc => {
			val personId = rc.getAuthedPersonId()
			PA.withParsedPostBodyJSON(request.body.asJson, ApClassUnenrollShape.apply)(parsed => {
				PortalLogic.apClassUnenroll(rc, personId, parsed.instanceId)
				Future(Ok(new JsObject(Map(
					"success" -> JsBoolean(true)
				))))
			})
		})
	}

	case class ApClassUnenrollShape (
		instanceId: Int
	)

	object ApClassUnenrollShape {
		implicit val format = Json.format[ApClassUnenrollShape]

		def apply(v: JsValue): ApClassUnenrollShape = v.as[ApClassUnenrollShape]
	}
}