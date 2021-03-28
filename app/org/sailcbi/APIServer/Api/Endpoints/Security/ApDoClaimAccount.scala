package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForUpdateOrDelete
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.BouncerRequestCache
import play.api.libs.json.{JsBoolean, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApDoClaimAccount @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, ApInitiateClaimAccountShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, rc => {


				val canClaim = PortalLogic.apCanClaim(rc, parsed.email) match {
					case Right(_) => ValidationOk
					case Left(s) => ValidationResult.from(s)
				}

				(for {
					_ <- canClaim
					e <- PortalLogic.validateClaimAcctHash(rc, parsed.email, parsed.personId, parsed.hash)
				} yield e) match {
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					case ValidationOk => {
						val update = new PreparedQueryForUpdateOrDelete(Set(BouncerRequestCache)) {
							override val params: List[String] = List(
								parsed.passwordHash,
								MagicIds.PW_HASH_SCHEME.MEMBER_2,
								parsed.personId.toString,
								parsed.email
							)
							override def getQuery: String =
								"""
								  |update persons set
								  |pw_hash = ?,
								  |pw_hash_scheme = ?
								  |where person_id = ? and lower(email) = ? and pw_hash is null
								  |""".stripMargin
						}

						rc.executePreparedQueryForUpdateOrDelete(update)

						Future(Ok(new JsObject(Map(
							"success" -> JsBoolean(true)
						))))
					}
				}
			})
		})
	}

	case class ApDoClaimAccountShape(email: String, personId: Int, hash: String, passwordHash: String)

	object ApInitiateClaimAccountShape {
		implicit val format = Json.format[ApDoClaimAccountShape]

		def apply(v: JsValue): ApDoClaimAccountShape = v.as[ApDoClaimAccountShape]
	}
}
