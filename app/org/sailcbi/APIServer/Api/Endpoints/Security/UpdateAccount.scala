package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{EmailUtil, ParsedRequest}
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.BouncerRequestCache
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdateAccount @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, UpdateAccountShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, rc => {


				validate(rc, parsed.oldEmail, parsed.newEmail) match {
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					case ValidationOk => {
						val q = new PreparedQueryForUpdateOrDelete(Set(BouncerRequestCache)) {
							override val params: List[String] = List(parsed.pwHash, parsed.newEmail, parsed.oldEmail)

							override def getQuery: String =
								"""
								  |  update persons p2
								  |  set p2.pw_hash = ?,
								  |  p2.email = lower(utl_url.unescape(?))
								  |  where p2.person_id = (select max(p.person_id) from persons p, (
								  |    select p1.person_id from persons p1 minus select ptd.person_id from persons_to_delete ptd
								  |  ) ilv
								  |  where p.person_id = ilv.person_id and lower(email) = lower(utl_url.unescape(?))
								  |  and pw_hash is not null)
								  |""".stripMargin
						}
						rc.executePreparedQueryForUpdateOrDelete(q)

						Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
					}
				}
			})
		})
	}

	def validate(rc: RequestCache[_], oldEmail: String, newEmail: String): ValidationResult = {
		val emailNotBlank = {
			if (newEmail == null || newEmail.length == 0) ValidationResult.from("Email may not be blank")
			else ValidationOk
		}

		val emailValid = {
			if (EmailUtil.regex.findFirstIn(newEmail).isDefined) ValidationOk
			else ValidationResult.from("Email is not valid.")
		}

		lazy val emailInUse = {
			val q = new PreparedQueryForSelect[Int](Set(BouncerRequestCache)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override val params: List[String] = List(newEmail)

				override def getQuery: String =
					"""
					  |select 1 from persons where lower(email) = lower(utl_url.unescape(?)) and pw_hash is not null
					  |""".stripMargin

			}
			if (rc.executePreparedQueryForSelect(q).nonEmpty) ValidationResult.from("There is already another account with that email address.")
			else ValidationOk
		}

		val unconditionalValidations = List(emailNotBlank, emailValid)

		val validations = {
			if (oldEmail.toLowerCase == newEmail.toLowerCase()) unconditionalValidations
			else emailInUse :: unconditionalValidations
		}

		ValidationResult.combine(validations)
	}


	case class UpdateAccountShape(
		oldEmail: String,
		newEmail: String,
		pwHash: String
	)

	object UpdateAccountShape {
		implicit val format = Json.format[UpdateAccountShape]

		def apply(v: JsValue): UpdateAccountShape = v.as[UpdateAccountShape]
	}

}
