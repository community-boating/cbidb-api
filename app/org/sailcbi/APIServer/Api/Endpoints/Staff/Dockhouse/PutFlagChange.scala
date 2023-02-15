package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Logic.DockhouseLogic.DockhouseLogic
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutFlagChange @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutFlagChangeShape.apply)(parsed => {
				DockhouseLogic.putFlagChange(rc, parsed.flagColor) match {
					case Left(err) => Future(Ok(ValidationResult.from(err).toResultError.asJsObject))
					case Right(_) => Future(Ok("OK"))
				}
			})
		})
	})

	case class PutFlagChangeShape(
		flagColor: String
	)

	object PutFlagChangeShape {
		implicit val format = Json.format[PutFlagChangeShape]

		def apply(v: JsValue): PutFlagChangeShape = v.as[PutFlagChangeShape]
	}
}
