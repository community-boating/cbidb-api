package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GrantRatings @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, GrantRatingsShape.apply)(parsed => {
				Future(Ok("OK"))
			})
		})
	})

	case class GrantRatingsShape(
		instructor: String,
		programId: Int,
		personIds: List[Int],
		ratingIds: List[Int]
	)

	object GrantRatingsShape {
		implicit val format = Json.format[GrantRatingsShape]
		def apply(v: JsValue): GrantRatingsShape = v.as[GrantRatingsShape]
	}
}
