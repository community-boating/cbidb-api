package org.sailcbi.APIServer.Api.Endpoints.Kiosk

import java.sql.ResultSet

import javax.inject.Inject
import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForSelect, PreparedQueryForInsert}
import org.sailcbi.APIServer.Services.Authentication.KioskUserType
import org.sailcbi.APIServer.Services.CacheBroker
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import play.api.libs.json.{JsNumber, JsObject, JsString}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class CreateCard @Inject()(implicit exec: ExecutionContext) extends AuthenticatedRequest {
	object errors {
		val NOT_JSON = JsObject(Map(
			"code" -> JsString("not_json"),
			"message" -> JsString("Post body not parsable as json.")
		))
		val BAD_PARAMS = JsObject(Map(
			"code" -> JsString("bad_parameters"),
			"message" -> JsString("JSON parameters were not correct.")
		))
		val CONTRAINT = JsObject(Map(
			"code" -> JsString("sql_constraint"),
			"message" -> JsString("Insert failed due to database constraint.  Probably that personId doesn't exist.")
		))
		val ACCESS_DENIED = JsObject(Map(
			"code" -> JsString("access_denied"),
			"message" -> JsString("Authentication failure.")
		))
		val UNKNOWN = JsObject(Map(
			"code" -> JsString("unknown"),
			"message" -> JsString("An unknwon error occurred.")
		))
	}

	def post: Action[AnyContent] = Action.async { request =>
		try {
			val rc = getRC(KioskUserType, ParsedRequest(request))
			val cb: CacheBroker = rc.cb
			val pb = rc.pb

			val params = request.body.asJson

			// TODO: if we're keeping this, redo with chained try's or something rather than pyramid of ifs
			if (params.isEmpty) {
				Future {
					Status(400)(JsObject(Map("error" -> errors.NOT_JSON)))
				}
			} else {
				try {
					val parsed = CreateCardParams.apply(params.get)
					println(parsed)
					val getCardNumber = new HardcodedQueryForSelect[Int](Set(KioskUserType)) {
						override def mapResultSetRowToCaseObject(rs: ResultSet): Int = rs.getInt(1)

						override def getQuery: String = "select GUEST_CARD_SEQ.nextval from dual"
					}

					val cardNumber = pb.executePreparedQueryForSelect(getCardNumber).head

					val getClose = new HardcodedQueryForSelect[Int](Set(KioskUserType)) {
						override def mapResultSetRowToCaseObject(rs: ResultSet): Int = rs.getInt(1)

						override def getQuery: String = "select c.close_id from fo_closes c, current_closes cur where close_id = inperson_close"
					}

					val closeId = pb.executePreparedQueryForSelect(getClose).head

					val createCard = new PreparedQueryForInsert(Set(KioskUserType)) {
						override def getQuery: String =
							"""
							  |insert into persons_cards
							  |(person_id, issue_date, card_num, temp, active, close_id, paid) values
							  | (?, sysdate, ?, 'N', 'Y', ?, 'N')
							""".stripMargin

						override val params: List[String] = List(
							parsed.personID.toString,
							cardNumber.toString,
							closeId.toString
						)
						override val pkName: Option[String] = Some("assign_id")
					}

					val assignID = pb.executePreparedQueryForInsert(createCard)

					Future {
						Ok(JsObject(Map(
							"cardAssignID" -> JsNumber(assignID.getOrElse("-1").toInt),
							"cardNumber" -> JsNumber(cardNumber)
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
							Status(400)(JsObject(Map("error" -> errors.BAD_PARAMS)))
						}
					}
					case e: Throwable => {
						println(e)
						Future {
							Status(400)(JsObject(Map("error" -> errors.UNKNOWN)))
						}
					}
				}

			}
		} catch {
			case _: UnauthorizedAccessException => Future {
				Status(400)(JsObject(Map("error" -> errors.ACCESS_DENIED)))
			}
			case e: Throwable => {
				println(e)
				Future {
					Status(400)(JsObject(Map("error" -> errors.UNKNOWN)))
				}
			}
		}
	}
}
