package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.json._
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SignupNote @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int, instanceId: Int)(implicit PA: PermissionsAuthority) = Action.async { request =>
		val logger = PA.logger
		val parsedRequest = ParsedRequest(request)
		MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, juniorId, rc => {
			PortalLogic.getSignupNote(rc, juniorId, instanceId) match {
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
			MemberRequestCache.withRequestCacheMemberWithJuniorId(parsedRequest, parsed.juniorId, rc => {

				implicit val format = SignupNoteShape.format

				PortalLogic.saveSignupNote(rc, parsed.juniorId, parsed.instanceId, parsed.signupNote) match {
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
			PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
				val username = rc.userName
				println("protoperson username is " + username)
				val parentPersonId = rc.getAuthedPersonId.get
				println("parent personId is " + parentPersonId)

				val juniorMatchesParent = {
					val q = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache)) {
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
					rc.executePreparedQueryForSelect(q).nonEmpty
				}

				if (!juniorMatchesParent) {
					val ve = ValidationResult.from("Unable to locate junior")
					Future(Ok(ve.toResultError.asJsObject()))
				} else {
					implicit val format = SignupNoteShape.format
					PortalLogic.saveSignupNote(rc, parsed.juniorId, parsed.instanceId, parsed.signupNote) match {
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
