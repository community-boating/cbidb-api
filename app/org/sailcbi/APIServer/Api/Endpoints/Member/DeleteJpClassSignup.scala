package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.{ValidationError, ValidationOk}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeleteJpClassSignup @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, JpClassSignupDeletePostShape.apply)(parsed => {
			MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, parsed.juniorId, rc => {
				println(parsed)

				PortalLogic.attemptDeleteSignup(rc, parsed.juniorId, parsed.instanceId) match {
					case ValidationOk => Future(Ok(new JsObject(Map("success" -> JsBoolean(true)))))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
				}
			})
		})
	}

	case class JpClassSignupDeletePostShape (
		juniorId: Int,
		instanceId: Int
	)

	object JpClassSignupDeletePostShape {
		implicit val format = Json.format[JpClassSignupDeletePostShape]

		def apply(v: JsValue): JpClassSignupDeletePostShape = v.as[JpClassSignupDeletePostShape]
	}
}
