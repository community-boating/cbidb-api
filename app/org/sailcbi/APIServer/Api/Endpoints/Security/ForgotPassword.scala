package org.sailcbi.APIServer.Api.Endpoints.Security

import java.sql.CallableStatement

import javax.inject.Inject
import org.sailcbi.APIServer.Api.ValidationResult
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForSelect}
import org.sailcbi.APIServer.Services.Authentication.BouncerUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import scala.concurrent.ExecutionContext

class ForgotPassword @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					new Status(400)("no body")
				}
				case Some(v: JsValue) => {
					val rc: RequestCache = PA.getRequestCache(BouncerUserType, None, parsedRequest).get
					println(v)
					val parsed = ForgotPasswordShape.apply(v)
					println(parsed)
					val pb = rc.pb

					val q = new PreparedQueryForSelect[Int](Set(BouncerUserType)) {
						override val params: List[String] = List(parsed.email)

						override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

						override def getQuery: String =
							"""
							  |  select 1 from persons p, (
							  |    select person_id from persons minus select person_id from persons_to_delete
							  |  ) ilv where p.person_id = ilv.person_id and lower(email) = lower(?) and pw_hash is not null and length(email) > 0
							  |""".stripMargin
					}

					val matchingRecords = pb.executePreparedQueryForSelect(q)

					if (matchingRecords.size != 1) {
						Ok(ValidationResult.from(
							"""
							  |No account was found with that email address.  If you are already a member,
							  |please call the Front Office at 617-523-1038 for assistance setting up your account.
							  |Otherwise you may return to the previous page to purchase a membership.
							  |""".stripMargin).toResultError.asJsObject())
					} else {
						val ppc = new PreparedProcedureCall[Unit](Set(BouncerUserType)) {
							override def setInParametersVarchar: Map[String, String] = Map(
								"p_email" -> parsed.email
							)

							override def setInParametersInt: Map[String, Int] = Map(
								"p_app_id" -> -1
							)
							override def registerOutParameters: Map[String, Int] = Map.empty

							override def getOutResults(cs: CallableStatement): Unit = Unit

							override def getQuery: String = "email_pkg.public_reset_pw(?, ?)"
						}

						pb.executeProcedure(ppc)


						Ok(JsObject(Map("success" -> JsBoolean(true))))
					}


				}
				case Some(v) => {
					println("wut dis " + v)
					Ok("wat")
				}
			}
		} catch {
			case _: UnauthorizedAccessException => Ok("Access Denied")
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Ok("Internal Error")
			}
		}
	}

	case class ForgotPasswordShape(email: String)

	object ForgotPasswordShape {
		implicit val format = Json.format[ForgotPasswordShape]

		def apply(v: JsValue): ForgotPasswordShape = v.as[ForgotPasswordShape]
	}

}
