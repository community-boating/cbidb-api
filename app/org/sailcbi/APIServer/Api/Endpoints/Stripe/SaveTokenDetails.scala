package org.sailcbi.APIServer.Api.Endpoints.Stripe

import javax.inject.Inject
import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.{CriticalError, NetSuccess, ParsedRequest, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{StripeError, Token}
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForInsert
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

// Called by react to get details and save to prod DB in one swoop
class SaveTokenDetails @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends AuthenticatedRequest {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req => try {
		val logger = PA.logger
		val pr = ParsedRequest(req)
		val rc = getRC(PublicUserType, pr)
		val pb = rc.pb
		val stripeIOController = rc.getStripeIOController(ws)

		pr.postJSON match {
			case Some(jsv: JsValue) => {
				val parsedBody = SaveTokenDetailsRequestShape(jsv)
				stripeIOController.getTokenDetails(parsedBody.token).map({
					case s: NetSuccess[Token, StripeError] => {
						println("Get token details success " + s.successObject)
						val insertQ = new PreparedQueryForInsert(Set(PublicUserType)) {
							override val params: List[String] = List(
								parsedBody.token,
								parsedBody.orderId.toString,
								s.successObject.card.last4,
								s.successObject.card.exp_month.toString,
								s.successObject.card.exp_year.toString,
								s.successObject.card.address_zip.getOrElse("")
							)
							override val pkName: Option[String] = None

							override def getQuery: String =
								"""
								  |  insert into stripe_tokens(
								  |    token, order_id, created_datetime, card_last_digits, card_exp_month, card_exp_year, card_zip, active
								  |  ) values (
								  |    ?, ?, util_pkg.get_sysdate, ?, ?, ?, ?, 'Y'
								  |  )
								  |""".stripMargin
						}

						pb.executePreparedQueryForInsert(insertQ)

						val response: StripeTokenSavedShape = StripeTokenSavedShape(
							parsedBody.token,
							parsedBody.orderId,
							s.successObject.card.last4,
							s.successObject.card.exp_month.toString,
							s.successObject.card.exp_year.toString,
							s.successObject.card.address_zip
						)

						implicit val format = StripeTokenSavedShape.format
						Ok(Json.toJson(response))
					}
					case v: ValidationError[Token, StripeError] => {
						logger.warning("Get token details validation error " + v.errorObject)
						Ok(List("failure", v.errorObject.`type`, v.errorObject.message).mkString("$$"))
					}
					case e: CriticalError[Token, StripeError] => {
						logger.error("Get token details critical error: ", e.e)
						Ok(List("failure", "cbi-api-error", e.e.getMessage).mkString("$$"))
					}
				})
			}
			case _ => Future(Ok("Unparseable body."))
		}
	} catch {
		case e: Throwable => {
			e.printStackTrace()
			Future(Ok("Internal Error."))
		}
	}}

	case class SaveTokenDetailsRequestShape(token: String, orderId: Int)

	object SaveTokenDetailsRequestShape {
		implicit val format = Json.format[SaveTokenDetailsRequestShape]

		def apply(v: JsValue): SaveTokenDetailsRequestShape = v.as[SaveTokenDetailsRequestShape]
	}
}
