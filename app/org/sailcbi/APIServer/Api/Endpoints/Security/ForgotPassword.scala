package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.neptune.API.ValidationResult
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForSelect}
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.UserTypes.BouncerRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import java.sql.CallableStatement
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ForgotPassword @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, ForgotPasswordShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, rc => {


				val q = new PreparedQueryForSelect[Int](Set(BouncerRequestCache)) {
					override val params: List[String] = List(parsed.email)

					override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

					override def getQuery: String =
						"""
						  |  select 1 from persons p, (
						  |    select person_id from persons minus select person_id from persons_to_delete
						  |  ) ilv where p.person_id = ilv.person_id and lower(email) = lower(?) and pw_hash is not null and length(email) > 0
						  |""".stripMargin
				}

				val matchingRecords = rc.executePreparedQueryForSelect(q)

				val appID: Int = parsed.program match {
					case "JP" => -1
					case "AP" => -2
					case x => {
						throw new Exception("Unrecognized program " + x)
					}
				}

				if (matchingRecords.size != 1) {
					Future(Ok(ValidationResult.from(
						"""
						  |No account was found with that email address.  If you are already a member,
						  |please call the Front Office at 617-523-1038 for assistance setting up your account.
						  |Otherwise you may return to the previous page to purchase a membership.
						  |""".stripMargin).toResultError.asJsObject()))
				} else {
					val ppc = new PreparedProcedureCall[Unit](Set(BouncerRequestCache)) {
						override def setInParametersVarchar: Map[String, String] = Map(
							"p_email" -> parsed.email
						)

						override def setInParametersInt: Map[String, Int] = Map(
							"p_app_id" -> appID
						)
						override def registerOutParameters: Map[String, Int] = Map.empty

						override def getOutResults(cs: CallableStatement): Unit = Unit

						override def getQuery: String = "email_pkg.public_reset_pw(?, ?)"
					}

					rc.executeProcedure(ppc)


					Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
				}
			})
		})
	}

	case class ForgotPasswordShape(email: String, program: String)

	object ForgotPasswordShape {
		implicit val format = Json.format[ForgotPasswordShape]

		def apply(v: JsValue): ForgotPasswordShape = v.as[ForgotPasswordShape]
	}

}
