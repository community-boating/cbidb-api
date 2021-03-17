package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonRequestCache
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
				val orderId = PortalLogic.getOrderId(rc, personId, parsed.program.get)

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
			if (parsed.program.contains("Common")) {
				PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
					val personId = rc.getAuthedPersonId().get
					val orderId = PortalLogic.getOrderId(rc, personId, "Donate")

					PortalLogic.deleteDonationFromOrder(rc, orderId, parsed.fundId)

					Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
				})
			} else {
				PA.withRequestCacheMember(parsedRequest, rc => {
					val personId = rc.getAuthedPersonId()
					val orderId = PortalLogic.getOrderId(rc, personId, parsed.program.get)

					PortalLogic.deleteDonationFromOrder(rc, orderId, parsed.fundId)

					Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
				})
			}
		})
	}

	def addStandalone()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AddRemoveDonationShape.apply)(parsed => {
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				val personId = rc.getAuthedPersonId() match {
					case Some(id) => id
					case None => PortalLogic.persistStandalonePurchaser(rc, rc.userName, None, None, None)
				}

				val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.DONATE)

				PortalLogic.addDonationToOrder(rc, orderId, parsed.fundId, parsed.amount, parsed.inMemoryOf) match{
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					case ValidationOk => Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
				}
			})
		})
	}

	case class AddRemoveDonationShape(
		fundId: Int,
		amount: Double,
		program: Option[String],
		inMemoryOf: Option[String]
	)

	object AddRemoveDonationShape {
		implicit val format = Json.format[AddRemoveDonationShape]

		def apply(v: JsValue): AddRemoveDonationShape = v.as[AddRemoveDonationShape]
	}

}