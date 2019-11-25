package org.sailcbi.APIServer.Api.Endpoints.Kiosk

import javax.inject.Inject
import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForSelect, PreparedQueryForInsert}
import org.sailcbi.APIServer.Services.Authentication.KioskUserType
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsNumber, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class CreateCard @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	object errors {

		val CONTRAINT = JsObject(Map(
			"code" -> JsString("sql_constraint"),
			"message" -> JsString("Insert failed due to database constraint.  Probably that personId doesn't exist.")
		))
	}

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request => {
		PA.withRequestCache(KioskUserType, None, ParsedRequest(request), rc => {
			val cb: CacheBroker = rc.cb
			val pb = rc.pb

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
					val getCardNumber = new HardcodedQueryForSelect[Int](Set(KioskUserType)) {
						override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

						override def getQuery: String = "select GUEST_CARD_SEQ.nextval from dual"
					}

					val cardNumber = pb.executePreparedQueryForSelect(getCardNumber).head

					val getClose = new HardcodedQueryForSelect[Int](Set(KioskUserType)) {
						override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)

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
