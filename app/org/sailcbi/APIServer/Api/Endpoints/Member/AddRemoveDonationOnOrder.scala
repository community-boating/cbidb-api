package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.EmailUtil
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddRemoveDonationOnOrder @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends InjectedController {
	def add()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, AddRemoveDonationShape.apply)(parsed => {
			PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
				val personId = rc.getAuthedPersonId
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
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				val stripe = rc.getStripeIOController(ws)
				val personId = PortalLogic.persistStandalonePurchaser(rc, rc.userName, rc.getAuthedPersonId, parsed.nameFirst, parsed.nameLast, parsed.email)

				val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.DONATE)

				PortalLogic.addDonationToOrder(rc, orderId, parsed.fundId, parsed.amount, parsed.inMemoryOf) match{
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					case ValidationOk => PortalLogic.setUsePaymentIntentDonationStandalone(rc, stripe, personId, orderId, parsed.doRecurring.getOrElse(false)).map(_ => {
						Ok(JsObject(Map("success" -> JsBoolean(true))))
					})
				}
			})
		})
	}

	def setStandalonePersonInfo()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, SetStandalonePersonShape.apply)(parsed => {
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				val stripe = rc.getStripeIOController(ws)
				runValidationstandalonePersonInfo(rc, parsed) match {
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
					case ValidationOk => {
						val personId = PortalLogic.persistStandalonePurchaser(rc, rc.userName, rc.getAuthedPersonId, parsed.nameFirst, parsed.nameLast, parsed.email)

						val orderId = PortalLogic.getOrderId(rc, personId, ORDER_NUMBER_APP_ALIAS.DONATE)

						PortalLogic.setUsePaymentIntentDonationStandalone(rc, stripe, personId, orderId, parsed.doRecurring).map(_ => {
							Ok(JsObject(Map("success" -> JsBoolean(true))))
						})
					}
				}
			})
		})
	}

	private def runValidationstandalonePersonInfo(rc: RequestCache, parsed: SetStandalonePersonShape): ValidationResult = {
		val unconditionals = List(
			ValidationResult.checkBlank(parsed.nameFirst, "First Name"),
			ValidationResult.checkBlank(parsed.nameLast, "Last Name"),
			ValidationResult.checkBlank(parsed.email, "Email"),
			ValidationResult.inline(parsed.email)(
				email => email.getOrElse("").isEmpty || EmailUtil.regex.findFirstIn(email.getOrElse("")).isDefined,
				"Email is not valid."
			),
		)

		val ifRecurring = List((
			parsed.doRecurring && parsed.email.isDefined,
			() => confirmNoAccount(rc, parsed.email.get)
		)).filter(_._1).map(_._2())

		ValidationResult.combine(unconditionals ::: ifRecurring)
	}

	private def confirmNoAccount(rc: RequestCache, email: String): ValidationResult = {
		val q = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override val params: List[String] = List(
				email.toLowerCase()
			)

			override def getQuery: String =
				"""
				  |select 1 from persons where lower(email) = ? and pw_hash is not null
				  |""".stripMargin
		}
		val exists = rc.executePreparedQueryForSelect(q).nonEmpty
		if (exists) {
			ValidationResult.from(
				"""
				  |An account for that email address already exists.  To set up a recurring donation, please <a target="_blank" href="/ap/donate">log in</a>.  If you are having trouble logging in,
				  |<a target="_blank" href="/ap/forgot-pw">click here</a> to reset your password.
				  |""".stripMargin)
		} else {
			ValidationOk
		}
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