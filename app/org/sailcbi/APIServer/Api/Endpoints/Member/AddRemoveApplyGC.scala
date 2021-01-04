package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddRemoveApplyGC @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def add()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, ApplyGCShape.apply)(parsed => {
			PA.withRequestCacheMember(None, parsedRequest, rc => {
				val pb = rc.pb
				val personId = rc.auth.getAuthedPersonId(rc)
				val orderId = PortalLogic.getOrderId(rc, personId)

				if (parsed.gcCode.isEmpty || parsed.gcNumber.isEmpty) {
					Future(Ok(ValidationResult.from("Gift Certificate number or code is invalid.").toResultError.asJsObject()))
				} else {
					PortalLogic.addGiftCertificateToOrder(rc, parsed.gcNumber.get, parsed.gcCode.get, orderId, PA.now.toLocalDate) match{
						case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
						case ValidationOk => Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
					}
				}
			})
		})
	}

	def delete()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, UnapplyGCShape.apply)(parsed => {
			PA.withRequestCacheMember(None, parsedRequest, rc => {
				val pb = rc.pb
				val personId = rc.auth.getAuthedPersonId(rc)
				val orderId = PortalLogic.getOrderId(rc, personId)

				PortalLogic.unapplyGCFromOrder(rc, orderId, parsed.certId)

				Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
			})
		})
	}

	case class ApplyGCShape(
		gcNumber: Option[Int],
		gcCode: Option[String]
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