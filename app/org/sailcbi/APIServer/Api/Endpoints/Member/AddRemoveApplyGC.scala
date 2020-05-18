package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class AddRemoveApplyGC @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def add()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, ApplyGCShape.apply)(parsed => {
			PA.withRequestCacheMember(None, parsedRequest, rc => {
				val pb: PersistenceBroker = rc.pb
				val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
				val orderId = PortalLogic.getOrderId(pb, personId)

				PortalLogic.addGiftCertificateToOrder(pb, parsed.gcNumber, parsed.gcCode, orderId, PA.now.toLocalDate) match{
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					case ValidationOk => Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
				}
			})
		})
	}

	def delete()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, UnapplyGCShape.apply)(parsed => {
			PA.withRequestCacheMember(None, parsedRequest, rc => {
				val pb: PersistenceBroker = rc.pb
				val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
				val orderId = PortalLogic.getOrderId(pb, personId)

				PortalLogic.unapplyGCFromOrder(pb, orderId, parsed.certId)

				Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
			})
		})
	}

	case class ApplyGCShape(
		gcNumber: Int,
		gcCode: String
	)

	object ApplyGCShape {
		implicit val format = Json.format[ApplyGCShape]

		def apply(v: JsValue): ApplyGCShape = v.as[ApplyGCShape]
	}

	case class UnapplyGCShape(
		certId: Int
	)

	object UnapplyGCShape{
		implicit val format = Json.format[UnapplyGCShape]

		def apply(v: JsValue): UnapplyGCShape = v.as[UnapplyGCShape]
	}
}