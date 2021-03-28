package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.framework.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.BouncerRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ResetPassword @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, ResetPasswordShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, rc => {


				validateHash(rc, parsed.email, parsed.hash) match {
					case ValidationOk => {
						markHashesUsed(rc, parsed.email)
						setNewPassword(rc, parsed.email, parsed.pwHash)
						Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
					}
					case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
				}
			})
		})
	}

	def validateHash(rc: RequestCache, email: String, hash: String): ValidationResult = {
		val q = new PreparedQueryForSelect[String](Set(BouncerRequestCache)) {
			override val params: List[String] = List(email)

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): String = rsw.getString(1)

			override def getQuery: String =
				"""
				  |select req_hash from reset_pw_reqs where person_user = 'P' and lower(email) = lower(utl_url.unescape(?)) and used = 'N'
				  |""".stripMargin

		}

		val dbHashResults = rc.executePreparedQueryForSelect(q)

		if (dbHashResults.size == 1 && dbHashResults.head == hash) ValidationOk
		else ValidationResult.from(
			"""
			  |We were unable to set your new password; this may be because you have reused an old password reset link (for security reasons the links are single-use only).
			  |If this is the case please click "Forgot my Password" again on the login page and be sure you are using the link from the most recent email.
			  |If this message persists please call the Front Office for assistance at 617-523-1038.
			  |""".stripMargin)
	}

	def markHashesUsed(rc: RequestCache, email: String): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(BouncerRequestCache)) {
			override val params: List[String] = List(email)

			override def getQuery: String =
				"""
				  |		update reset_pw_reqs
				  |				set used = 'Y'
				  |		where person_user = 'P'
				  |		and lower(email) = lower(utl_url.unescape(?))
				  |		and used = 'N'
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)
	}

	def setNewPassword(rc: RequestCache, email: String, pwHash: String): Unit = {
		val q = new PreparedQueryForUpdateOrDelete(Set(BouncerRequestCache)) {
			override val params: List[String] = List(pwHash, MagicIds.PW_HASH_SCHEME.MEMBER_2, email)

			override def getQuery: String =
				"""
				  |  update persons p2
				  |  set p2.pw_hash = ?,
				  |  p2.pw_hash_scheme = ?
				  |  where p2.person_id = (select max(p.person_id) from persons p, (
				  |    select p1.person_id from persons p1 minus select ptd.person_id from persons_to_delete ptd
				  |  ) ilv
				  |  where p.person_id = ilv.person_id and lower(email) = lower(utl_url.unescape(?))
				  |  and pw_hash is not null)
				  |""".stripMargin
		}
		rc.executePreparedQueryForUpdateOrDelete(q)
	}

	case class ResetPasswordShape(email: String, hash: String, pwHash: String)

	object ResetPasswordShape {
		implicit val format = Json.format[ResetPasswordShape]

		def apply(v: JsValue): ResetPasswordShape = v.as[ResetPasswordShape]
	}

}
