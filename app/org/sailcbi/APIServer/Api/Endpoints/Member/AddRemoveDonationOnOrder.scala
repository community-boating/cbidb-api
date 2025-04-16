package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.neptune.Util.EmailUtil
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.{PersonExistsException, PortalLogic}
import org.sailcbi.APIServer.UserTypes.{MemberMaybeOrProtoPersonRequestCache, MemberMaybeRequestCache, MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddRemoveDonationOnOrder @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def add()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AddRemoveDonationShape.apply)(parsed => {
			MemberMaybeOrProtoPersonRequestCache.getRC(PA, parsedRequest, rc => {
				val personId = rc.getAuthedPersonId.get
				val orderId = PortalLogic.getOrderId(rc, personId, parsed.program.get)
				PortalLogic.setUsePaymentIntent(rc, orderId, parsed.doRecurring.getOrElse(false))
				PortalLogic.addDonationToOrder(rc, orderId, parsed.fundId, parsed.amount) match{
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
					case ValidationOk => Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
				}
			})
		})
	}

	def delete()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AddRemoveDonationShape.apply)(parsed => {
			if (parsed.program.contains(ORDER_NUMBER_APP_ALIAS.DONATE)) {
				MemberMaybeOrProtoPersonRequestCache.getRC(PA, parsedRequest, rc => {
          val personId = rc.getAuthedPersonId.get
          val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.DONATE)

          PortalLogic.deleteDonationFromOrder(rc, orderId, parsed.fundId)

          Future(Ok(JsObject(Map("Success" -> JsBoolean(true)))))
        })
			} else {
				PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
					val personId = rc.getAuthedPersonId
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
			MemberMaybeOrProtoPersonRequestCache.getRCWithProtoPersonRC(PA, parsedRequest, (rc, ppRC) => {
				try {
					val personId = PortalLogic.persistStandalonePurchaser(rc, ppRC.userName, ppRC.getAuthedPersonId, parsed.nameFirst, parsed.nameLast, parsed.email)

					val orderId = PortalLogic.getOrderId(rc, rc.getAuthedPersonId.getOrElse(personId), ORDER_NUMBER_APP_ALIAS.DONATE)

					PortalLogic.setUsePaymentIntent(rc, orderId, parsed.doRecurring.getOrElse(false))

					PortalLogic.addDonationToOrder(rc, orderId, parsed.fundId, parsed.amount, parsed.inMemoryOf) match {
						case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
						case ValidationOk => Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
					}

				}catch {
					case e: PersonExistsException => {
						Future(Ok(e.result.toResultError.asJsObject))
					}
				}
			})
		})
	}

	def setStandalonePersonInfo()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, SetStandalonePersonShape.apply)(parsed => {
			MemberMaybeOrProtoPersonRequestCache.getRCWithProtoPersonRC(PA, parsedRequest, (rc, ppRC) => {
				runValidationstandalonePersonInfo(rc, parsed, ppRC.getAuthedPersonId) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject))
					case ValidationOk => {
						try {
							val personId = PortalLogic.persistStandalonePurchaser(rc, ppRC.userName, ppRC.getAuthedPersonId, parsed.nameFirst, parsed.nameLast, parsed.email)
							val orderId = PortalLogic.getOrderId(rc, rc.getAuthedPersonId.getOrElse(personId), ORDER_NUMBER_APP_ALIAS.DONATE)
							PortalLogic.setUsePaymentIntent(rc, orderId, parsed.doRecurring)
							Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
						}catch {
							case e: PersonExistsException => {
								Future(Ok(e.result.toResultError.asJsObject))
							}
						}
					}
				}
			})
		})
	}

	private def runValidationstandalonePersonInfo(rc: RequestCache, parsed: SetStandalonePersonShape, protoPersonId: Option[Int]): ValidationResult = {
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
		email: Option[String],
		doRecurring: Boolean,
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
		doRecurring: Option[Boolean],
	)

	object AddRemoveDonationShape {
		implicit val format = Json.format[AddRemoveDonationShape]

		def apply(v: JsValue): AddRemoveDonationShape = v.as[AddRemoveDonationShape]
	}

}