package org.sailcbi.APIServer.Api.Endpoints.Kiosk

import com.coleji.framework.API.ResultError
import com.coleji.framework.Core.{CacheBroker, ParsedRequest, PermissionsAuthority}
import com.coleji.framework.IO.PreparedQueries.{HardcodedQueryForSelect, PreparedQueryForInsert}
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.BarcodeFactory
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.KioskRequestCache
import play.api.libs.json.{JsNumber, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateCard @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	object errors {
		val CONTRAINT = JsObject(Map(
			"code" -> JsString("sql_constraint"),
			"message" -> JsString("Insert failed due to database constraint.  Probably that personId doesn't exist.")
		))
	}

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		PA.withRequestCache(KioskRequestCache)(None, ParsedRequest(request), rc => {
			val cb: CacheBroker = rc.cb

			val params = request.body.asJson

			// TODO: if we're keeping this, redo with chained try's or something rather than pyramid of ifs
			if (params.isEmpty) {
				Future {
					Status(400)(JsObject(Map("error" -> ResultError.NOT_JSON)))
				}
			} else {
				try {
					val parsed = CreateCardParams.apply(params.get)
					println(parsed)

					val (cardNumber, assignId, nonce) = PortalLogic.createGuestCard(rc, parsed.personID)

					Future {
						Ok(JsObject(Map(
							"cardAssignID" -> JsNumber(assignId),
							"cardNumber" -> JsNumber(cardNumber),
						)))
					}
				} catch {
					case e: java.sql.SQLIntegrityConstraintViolationException => {
						println(e)
						Future {
							Status(400)(JsObject(Map("error" -> errors.CONTRAINT)))
						}
					}
					case e: play.api.libs.json.JsResultException => {
						println(e)
						Future {
							Status(400)(JsObject(Map("error" -> ResultError.BAD_PARAMS)))
						}
					}
					case e: Throwable => {
						println(e)
						Future {
							Status(400)(JsObject(Map("error" -> ResultError.UNKNOWN)))
						}
					}
				}

			}
		})
	}}
}
