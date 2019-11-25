package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, ResultSetWrapper}
import play.api.libs.json._
import play.api.mvc.InjectedController

import scala.concurrent.{ExecutionContext, Future}

class SignupNote @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int, instanceId: Int)(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId).get
			val pb = rc.pb

			JPPortal.getSignupNote(pb, juniorId, instanceId) match {
				case Right(os) => os match {
					case Some(s) => Ok(new JsObject(Map(
						"juniorId" -> JsNumber(juniorId),
						"instanceId" -> JsNumber(instanceId),
						"signupNote" -> JsString(s)

					)))
					case None => Ok(new JsObject(Map(
						"juniorId" -> JsNumber(juniorId),
						"instanceId" -> JsNumber(instanceId),
						"signupNote" -> JsNull

					)))
				}
				case Left(err) => Ok(err.toResultError.asJsObject())
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
					println(v)
					val parsed = SignupNoteShape.apply(v)
					val rc: RequestCache = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, parsed.juniorId).get
					val pb = rc.pb
					implicit val format = SignupNoteShape.format

					JPPortal.saveSignupNote(pb, parsed.juniorId, parsed.instanceId, parsed.signupNote) match {
						case ValidationOk => Ok(Json.toJson(parsed))
						case e: ValidationError => Ok(e.toResultError.asJsObject())
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
	def postProto()(implicit PA: PermissionsAuthority) = Action.async { request =>
		try {
			val logger = PA.logger
			val parsedRequest = ParsedRequest(request)
			val data = request.body.asJson
			data match {
				case None => {
					println("no body")
					Future(new Status(400)("no body"))
				}
				case Some(v: JsValue) => {
					println(v)
					val parsed = SignupNoteShape.apply(v)
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
							JPPortal.saveSignupNote(pb, parsed.juniorId, parsed.instanceId, parsed.signupNote) match {
								case ValidationOk => Future(Ok(Json.toJson(parsed)))
								case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
							}
						}
					})

				}
				case Some(v) => {
					println("wut dis " + v)
					Future(Ok("wat"))
				}
			}
		} catch {
			case _: UnauthorizedAccessException => Future(Ok("Access Denied"))
			case e: Throwable => {
				println(e)
				e.printStackTrace()
				Future(Ok("Internal Error"))
			}
		}
	}

	case class SignupNoteShape(juniorId: Int, instanceId: Int, signupNote: Option[String])

	object SignupNoteShape{
		implicit val format = Json.format[SignupNoteShape]

		def apply(v: JsValue): SignupNoteShape = v.as[SignupNoteShape]
	}
}
