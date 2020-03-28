package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json._
import play.api.mvc.InjectedController

import scala.concurrent.{ExecutionContext, Future}

class SignupNote @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int, instanceId: Int)(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId, rc => {
			val pb = rc.pb

			PortalLogic.getSignupNote(pb, juniorId, instanceId) match {
				case Right(os) => os match {
					case Some(s) => Future(Ok(new JsObject(Map(
						"juniorId" -> JsNumber(juniorId),
						"instanceId" -> JsNumber(instanceId),
						"signupNote" -> JsString(s)

					))))
					case None => Future(Ok(new JsObject(Map(
						"juniorId" -> JsNumber(juniorId),
						"instanceId" -> JsNumber(instanceId),
						"signupNote" -> JsNull

					))))
				}
				case Left(err) => Future(Ok(err.toResultError.asJsObject()))
			}
		})
	}
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		val data = request.body.asJson
		PA.withParsedPostBodyJSON(request.body.asJson, SignupNoteShape.apply)(parsed => {
			PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId, rc => {
				val pb = rc.pb
				implicit val format = SignupNoteShape.format

				PortalLogic.saveSignupNote(pb, parsed.juniorId, parsed.instanceId, parsed.signupNote) match {
					case ValidationOk => Future(Ok(Json.toJson(parsed)))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
				}
			})
		})
	}
	def postProto()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		val data = request.body.asJson
		PA.withParsedPostBodyJSON(request.body.asJson, SignupNoteShape.apply)(parsed => {
			PA.withRequestCache(ProtoPersonUserType, None, parsedRequest, rc => {
				val username = rc.auth.userName
				println("protoperson username is " + username)
				val pb = rc.pb
				val parentPersonId = ProtoPersonUserType.getAuthedPersonId(username, pb).get
				println("parent personId is " + parentPersonId)

				val juniorMatchesParent = {
					val q = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType)) {
						override val params: List[String] = List(
							parentPersonId.toString,
							parsed.juniorId.toString
						)

						override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

						override def getQuery: String =
							s"""
							   |select 1 from person_relationships where a = ? and b = ?
							   |and type_id = ${MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK}
							   |""".stripMargin
					}
					pb.executePreparedQueryForSelect(q).nonEmpty
				}

				if (!juniorMatchesParent) {
					val ve = ValidationResult.from("Unable to locate junior")
					Future(Ok(ve.toResultError.asJsObject()))
				} else {
					implicit val format = SignupNoteShape.format
					PortalLogic.saveSignupNote(pb, parsed.juniorId, parsed.instanceId, parsed.signupNote) match {
						case ValidationOk => Future(Ok(Json.toJson(parsed)))
						case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					}
				}
			})
		})
	}

	case class SignupNoteShape(juniorId: Int, instanceId: Int, signupNote: Option[String])

	object SignupNoteShape{
		implicit val format = Json.format[SignupNoteShape]

		def apply(v: JsValue): SignupNoteShape = v.as[SignupNoteShape]
	}
}
