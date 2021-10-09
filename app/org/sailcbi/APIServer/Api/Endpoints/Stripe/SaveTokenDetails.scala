package org.sailcbi.APIServer.Api.Endpoints.Stripe

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForInsert
import com.coleji.neptune.Util.{CriticalError, NetSuccess, ValidationError}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{StripeError, Token}
import org.sailcbi.APIServer.Entities.Misc.StripeTokenSavedShape
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

// Called by react to get details and save to prod DB in one swoop
class SaveTokenDetails @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val logger = PA.logger
		val pr = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, pr, rc => {

			val stripeIOController = rc.getStripeIOController(ws)

			pr.postJSON match {
				case Some(jsv: JsValue) => {
					val parsedBody = SaveTokenDetailsRequestShape(jsv)
					stripeIOController.getTokenDetails(parsedBody.token).map({
						case s: NetSuccess[Token, StripeError] => {
							println("Get token details success " + s.successObject)
							val insertQ = new PreparedQueryForInsert(Set(PublicRequestCache)) {
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

							rc.executePreparedQueryForInsert(insertQ)

							val response: StripeTokenSavedShape = StripeTokenSavedShape(
								parsedBody.token,
								parsedBody.orderId,
								s.successObject.card.last4,
								s.successObject.card.exp_month,
								s.successObject.card.exp_year,
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
		})
	}

	case class SaveTokenDetailsRequestShape(token: String, orderId: Int)

	object SaveTokenDetailsRequestShape {
		implicit val format = Json.format[SaveTokenDetailsRequestShape]

		def apply(v: JsValue): SaveTokenDetailsRequestShape = v.as[SaveTokenDetailsRequestShape]
	}
}
