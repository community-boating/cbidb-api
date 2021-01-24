package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.Api.ValidationResult
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.BouncerRequestCache
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApInitiateClaimAccount @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, ApInitiateClaimAccountShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, rc => {


				PortalLogic.apCanClaim(rc, parsed.email) match {
					case Left(e) => Future(Ok(ValidationResult.from(e).toResultError.asJsObject()))
					case Right(personId) => {
						PortalLogic.apDoClaim(rc, personId)
						Future(Ok(new JsObject(Map(
							"success" -> JsBoolean(true)
						))))
					}
				}
			})
		})
	}

	case class ApInitiateClaimAccountShape(email: String)

	object ApInitiateClaimAccountShape {
		implicit val format = Json.format[ApInitiateClaimAccountShape]

		def apply(v: JsValue): ApInitiateClaimAccountShape = v.as[ApInitiateClaimAccountShape]
	}
}