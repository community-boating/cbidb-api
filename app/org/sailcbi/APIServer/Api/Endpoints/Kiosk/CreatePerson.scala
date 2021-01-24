package org.sailcbi.APIServer.Api.Endpoints.Kiosk

import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.{DateUtil, GetSQLLiteral, ParsedRequest}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForInsert
import org.sailcbi.APIServer.Services.Authentication.KioskUserType
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}
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
	}

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache[KioskUserType](KioskUserType)(None, ParsedRequest(request), rc => {
			val cb: CacheBroker = rc.cb

			val params = request.body.asJson

			// TODO: if we're keeping this, redo with chained try's or something rather than pyramid of ifs
			if (params.isEmpty) {
				Future {
					Status(400)(JsObject(Map("error" -> errors.NOT_JSON)))
				}
			} else {
				try {
					val parsed = CreatePersonParams.apply(params.get)
					val parsedDOB = DateUtil.parse(parsed.dob)
					println(parsed)
					val q = new PreparedQueryForInsert(Set(KioskUserType)) {
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
							   |previous_member
							   |) values
							   | ('N',?,?,to_date(?,'MM/DD/YYYY'),?,?,?,?,${GetSQLLiteral(parsed.previousMember)})
		  """.stripMargin

						override val params: List[String] = List(
							parsed.firstName,
							parsed.lastName,
							parsed.dob,
							parsed.emailAddress,
							parsed.phonePrimary,
							parsed.emerg1Name,
							parsed.emerg1PhonePrimary
						)
						override val pkName: Option[String] = Some("person_id")
					}
					val id = rc.executePreparedQueryForInsert(q)

					Future {
						Ok(JsObject(Map("personID" -> JsNumber(id.getOrElse("-1").toInt))))
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
}
