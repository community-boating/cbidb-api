package org.sailcbi.APIServer.Api.Endpoints.Kiosk

import com.coleji.framework.API.ResultError
import com.coleji.framework.Core.{CacheBroker, ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForInsert
import com.coleji.framework.Storable.GetSQLLiteral
import com.coleji.framework.Util.DateUtil
import org.sailcbi.APIServer.BarcodeFactory
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{KioskRequestCache, ProtoPersonRequestCache}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreatePerson @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	object errors {
		val NOT_JSON = JsObject(Map(
			"code" -> JsString("not_json"),
			"message" -> JsString("Post body not parsable as json.")
		))
		val BAD_PARAMS = JsObject(Map(
			"code" -> JsString("bad_parameters"),
			"message" -> JsString("JSON parameters were not correct.")
		))
		val BAD_DATE = JsObject(Map(
			"code" -> JsString("bad_date_format"),
			"message" -> JsString("DOB was not parsable as MM/DD/YYYY")
		))
		val CONTRAINT = JsObject(Map(
			"code" -> JsString("sql_constraint"),
			"message" -> JsString("Insert failed due to database constraint.  Probably that personId doesn't exist.")
		))
		val DUPLICATE = JsObject(Map(
			"code" -> JsString("duplicate_request"),
			"message" -> JsString("Duplicate request.")
		))
	}

	def postPortal()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache(ProtoPersonRequestCache)(None, ParsedRequest(request), rc => {
			val cb: CacheBroker = rc.cb

			val params = request.body.asJson

			if (params.isEmpty) {
				Future {
					Status(400)(JsObject(Map("error" -> errors.NOT_JSON)))
				}
			} else if (!rc.isStillProto()) {
				Future {
					Status(400)(JsObject(Map("error" -> errors.DUPLICATE)))
				}
			} else {
				try {
					val parsed = CreatePersonParams.apply(params.get)
					val id = doPost(rc, parsed, Some(rc.userName))
					val (cardNumber, assignId, nonce) = PortalLogic.createGuestCard(rc, id)
					val ticketHTML = PortalLogic.ticketHTML(cardNumber, nonce, parsed.firstName + " " + parsed.lastName)
					PortalLogic.sendTicketEmail(rc, parsed.emailAddress, ticketHTML)
					Future {
						Ok(JsObject(Map(
							"cardAssignID" -> JsNumber(assignId),
							"cardNumber" -> JsNumber(cardNumber),
							"ticketHTML" -> JsString(ticketHTML)
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
					case e: java.time.format.DateTimeParseException => {
						println(e)
						Future {
							Status(400)(JsObject(Map("error" -> errors.BAD_DATE)))
						}
					}
					case e: Throwable => {
						e.printStackTrace()
						println("this is the err")
						Future {
							Status(400)(JsObject(Map("error" -> ResultError.UNKNOWN)))
						}
					}
				}
			}
		})
	}

	def postKiosk()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache(KioskRequestCache)(None, ParsedRequest(request), rc => {
			val cb: CacheBroker = rc.cb

			val params = request.body.asJson

			if (params.isEmpty) {
				Future {
					Status(400)(JsObject(Map("error" -> errors.NOT_JSON)))
				}
			} else {
				try {
					val parsed = CreatePersonParams.apply(params.get)
					val id = doPost(rc, parsed, None)
						Future {
						Ok(JsObject(Map("personID" -> JsNumber(id))))
					}
				} catch {
					case e: play.api.libs.json.JsResultException => {
						println(e)
						Future {
							Status(400)(JsObject(Map("error" -> errors.BAD_PARAMS)))
						}
					}
					case e: java.time.format.DateTimeParseException => {
						println(e)
						Future {
							Status(400)(JsObject(Map("error" -> errors.BAD_DATE)))
						}
					}
					case e: Throwable => {
						e.printStackTrace()
						println("this is the err")
						Future {
							Status(400)(JsObject(Map("error" -> ResultError.UNKNOWN)))
						}
					}
				}
			}
		})
	}

	private def doPost(rc: RequestCache, parsed: CreatePersonParams, cookieValue: Option[String])(implicit PA: PermissionsAuthority): Int = {
		val parsedDOB = DateUtil.parse(parsed.dob)
		val q = new PreparedQueryForInsert(Set(ProtoPersonRequestCache, KioskRequestCache)) {
			override def getQuery: String =
				s"""
				   |insert into persons (
				   |temp,
				   |name_first,
				   |name_last,
				   |dob,
				   |email,
				   |phone_primary,
				   |emerg1_name,
				   |emerg1_phone_primary,
				   |previous_member,
				   |proto_state,
				   |protoperson_cookie
				   |) values
				   | ('N',?,?,to_date(?,'MM/DD/YYYY'),?,?,?,?,${GetSQLLiteral(parsed.previousMember)},'W',?)
							""".stripMargin

			override val params: List[String] = List(
				parsed.firstName,
				parsed.lastName,
				parsed.dob,
				parsed.emailAddress,
				parsed.phonePrimary,
				parsed.emerg1Name,
				parsed.emerg1PhonePrimary,
				cookieValue.orNull
			)
			override val pkName: Option[String] = Some("person_id")
		}
		rc.executePreparedQueryForInsert(q).get.toInt
	}
}
