package org.sailcbi.APIServer.Api.Endpoints.Kiosk

import com.coleji.neptune.API.ResultError
import com.coleji.neptune.Core.{CacheBroker, ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForInsert
import com.coleji.neptune.Storable.GetSQLLiteral
import com.coleji.neptune.Util.DateUtil
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
					val forRental = parsed.forRental.getOrElse(false)
					val guestPortalRegValue = if (forRental) "R" else "Y"
					val id = doPost(rc, parsed, Some(rc.userName), guestPortalRegValue)
					val (cardNumber, assignId, nonce) = PortalLogic.createGuestCard(rc, id)
					val ticketHTML = PortalLogic.ticketHTML(cardNumber, nonce, parsed.firstName + " " + parsed.lastName, forRental)
					PortalLogic.sendTicketEmail(rc, parsed.emailAddress, ticketHTML, forRental)
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
							Status(400)(JsObject(Map("error" -> ResultError.UNKNOWN.asJsObject)))
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
					val id = doPost(rc, parsed, None, null)
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
							Status(400)(JsObject(Map("error" -> ResultError.UNKNOWN.asJsObject)))
						}
					}
				}
			}
		})
	}

	private def doPost(rc: RequestCache, parsed: CreatePersonParams, cookieValue: Option[String], guestPortalReg: String)
		(implicit PA: PermissionsAuthority): Int = {
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
				   |protoperson_cookie,
				   |EMERG1_RELATION,
				   |PHONE_PRIMARY_TYPE,
				   |EMERG1_PHONE_PRIMARY_TYPE,
				   |guest_portal_reg
				   |) values
				   | ('N',?,?,to_date(?,'MM/DD/YYYY'),?,?,?,?,${GetSQLLiteral(parsed.previousMember)},'W',?,?,?,?, ?)
							""".stripMargin

			override val params: List[String] = List(
				parsed.firstName,
				parsed.lastName,
				parsed.dob,
				parsed.emailAddress,
				parsed.phonePrimary,
				parsed.emerg1Name,
				parsed.emerg1PhonePrimary,
				cookieValue.orNull,
				parsed.emerg1Relation.orNull,
				parsed.phonePrimaryType.orNull,
				parsed.emerg1PhonePrimaryType.orNull,
				guestPortalReg
			)
			override val pkName: Option[String] = Some("person_id")
		}
		rc.executePreparedQueryForInsert(q).get.toInt
	}
}
