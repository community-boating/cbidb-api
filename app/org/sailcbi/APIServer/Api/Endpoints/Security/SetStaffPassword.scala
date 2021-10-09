package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.neptune.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.EmailUtil
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.BouncerRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SetStaffPassword @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(request.body.asJson, SetStaffPasswordShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, rc => {
				val q = new PreparedQueryForUpdateOrDelete(Set(BouncerRequestCache)) {
					override val params: List[String] = List(parsed.pwHash, MagicIds.PW_HASH_SCHEME.STAFF_2, parsed.username)

					override def getQuery: String =
						"""
						  |  update users
						  |  set pw_hash = ?, pw_hash_scheme = ?
						  |  where upper(user_name) = upper(?)
						  |""".stripMargin
				}
				rc.executePreparedQueryForUpdateOrDelete(q)

				Future(Ok(JsObject(Map("success" -> JsBoolean(true)))))
			})
		})
	}

	case class SetStaffPasswordShape(
		username: String,
		pwHash: String
	)

	object SetStaffPasswordShape {
		implicit val format = Json.format[SetStaffPasswordShape]

		def apply(v: JsValue): SetStaffPasswordShape = v.as[SetStaffPasswordShape]
	}
}
