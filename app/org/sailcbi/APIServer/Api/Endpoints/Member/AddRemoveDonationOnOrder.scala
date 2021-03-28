package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.PermissionsAuthority
import org.sailcbi.APIServer.CbiUtil.{EmailUtil, ParsedRequest}
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.ProtoPersonRequestCache
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
			if (parsed.program.contains(ORDER_NUMBER_APP_ALIAS.DONATE)) {
				PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
					val personId = rc.getAuthedPersonId().get
					val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.DONATE)

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
				val personId = PortalLogic.persistStandalonePurchaser(rc, rc.userName, rc.getAuthedPersonId(), parsed.nameFirst, parsed.nameLast, parsed.email)

				val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.DONATE)

				PortalLogic.addDonationToOrder(rc, orderId, parsed.fundId, parsed.amount, parsed.inMemoryOf) match{
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					case ValidationOk => Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
				}
			})
		})
	}

	def setStandalonePersonInfo()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, SetStandalonePersonShape.apply)(parsed => {
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				runValidationstandalonePersonInfo(parsed) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
					case ValidationOk => {
						PortalLogic.persistStandalonePurchaser(rc, rc.userName, rc.getAuthedPersonId(), parsed.nameFirst, parsed.nameLast, parsed.email)

						Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
					}
				}


			})
		})
	}

	private def runValidationstandalonePersonInfo(parsed: SetStandalonePersonShape): ValidationResult = {
		val unconditionals = List(
			ValidationResult.checkBlank(parsed.nameFirst, "First Name"),
			ValidationResult.checkBlank(parsed.nameLast, "Last Name"),
			ValidationResult.checkBlank(parsed.email, "Email"),
			ValidationResult.inline(parsed.email)(
				email => email.getOrElse("").isEmpty || EmailUtil.regex.findFirstIn(email.getOrElse("")).isDefined,
				"Email is not valid."
			),
		)

		ValidationResult.combine(unconditionals)
	}

	case class SetStandalonePersonShape(
		nameFirst: Option[String],
		nameLast: Option[String],
		email: Option[String]
	)

	object SetStandalonePersonShape{
		implicit val format = Json.format[SetStandalonePersonShape]

		def apply(v: JsValue): SetStandalonePersonShape = v.as[SetStandalonePersonShape]
	}

	case class AddRemoveDonationShape(
		fundId: Int,
		amount: Double,
		program: Option[String],
		inMemoryOf: Option[String],
		nameFirst: Option[String],
		nameLast: Option[String],
		email: Option[String],
	)

	object AddRemoveDonationShape {
		implicit val format = Json.format[AddRemoveDonationShape]

		def apply(v: JsValue): AddRemoveDonationShape = v.as[AddRemoveDonationShape]
	}

}