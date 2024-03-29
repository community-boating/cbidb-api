package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Logic.DockhouseLogic.DockhouseLogic
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddPersonToClass @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, AddPersonToClassShape.apply)(parsed => {
				DockhouseLogic.addPersonToApClass(rc, parsed.personId, parsed.instanceId)
				Future(Ok("OK"))
			})
		})
	})

	case class AddPersonToClassShape(
		personId: Int,
		instanceId: Int
	)

	object AddPersonToClassShape {
		implicit val format = Json.format[AddPersonToClassShape]
		def apply(v: JsValue): AddPersonToClassShape = v.as[AddPersonToClassShape]
	}
}
