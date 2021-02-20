package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddRemoveDonationOnOrder @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def add()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AddRemoveDonationShape.apply)(parsed => {
			PA.withRequestCacheMember(parsedRequest, rc => {
				val personId = rc.getAuthedPersonId()
				val orderId = PortalLogic.getOrderId(rc, personId, parsed.program)

				PortalLogic.addDonationToOrder(rc, orderId, parsed.fundId, parsed.amount) match{
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					case ValidationOk => Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
				}
			})
		})
	}

	def delete()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AddRemoveDonationShape.apply)(parsed => {
			PA.withRequestCacheMember(parsedRequest, rc => {
				val personId = rc.getAuthedPersonId()
				val orderId = PortalLogic.getOrderId(rc, personId, parsed.program)

				PortalLogic.deleteDonationFromOrder(rc, orderId, parsed.fundId)

				Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
			})
		})
	}

	case class AddRemoveDonationShape(
		fundId: Int,
		amount: Double,
		program: String
	)

	object AddRemoveDonationShape {
		implicit val format = Json.format[AddRemoveDonationShape]

		def apply(v: JsValue): AddRemoveDonationShape = v.as[AddRemoveDonationShape]
	}

}