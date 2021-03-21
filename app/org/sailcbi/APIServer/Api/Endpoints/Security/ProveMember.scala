package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete, PreparedValue}
import org.sailcbi.APIServer.Services.Authentication.{BouncerRequestCache, ProtoPersonRequestCache}
import org.sailcbi.APIServer.Services.{PermissionsAuthority, ResultSetWrapper}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ProveMember @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, ProveMemberPostShape.apply)(parsed => {
			PA.withRequestCache(BouncerRequestCache)(None, parsedRequest, bouncerRC => {
				PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, protoRC => {
					val authedEmail = parsed.username
					val personQ = new PreparedQueryForSelect[(Option[String], Option[String], Int)](Set(BouncerRequestCache)) {
						override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): (Option[String], Option[String], Int) =
							(rsw.getOptionString(1), rsw.getOptionString(2), rsw.getInt(3))

						override def getParams: List[PreparedValue] = List(authedEmail)

						override def getQuery: String =
							"""
							  |select name_first, name_last, person_id
							  |from persons where email = ? and pw_hash is not null
							  |""".stripMargin
					}
					val (nameFirst, nameLast, existingPersonId) = bouncerRC.executePreparedQueryForSelect(personQ).head
					val protoPersonId = PortalLogic.persistStandalonePurchaser(protoRC, protoRC.userName, protoRC.getAuthedPersonId(), None, None, None)

					val updateProtoQ = new PreparedQueryForUpdateOrDelete(Set(BouncerRequestCache)) {
						override val params: List[String] = List(
							existingPersonId.toString,
							nameFirst.orNull,
							nameLast.orNull,
							authedEmail,
						)

						override def getQuery: String =
							s"""
							  |update persons
							  |set has_authed_as = ?,
							  |name_first = ?,
							  |name_last = ?,
							  |email = ?
							  |where person_id = $protoPersonId
							  |""".stripMargin
					}

					bouncerRC.executePreparedQueryForUpdateOrDelete(updateProtoQ)
					Future(Ok("true"))
				})
			})
		})
	}

	def detach()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
			rc.getAuthedPersonId() match {
				case Some(personId) => {
					val q = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
						override def getQuery: String =
							s"""
							  |update persons set has_authed_as = null
							  |where person_id = $personId
							  |""".stripMargin
					}
					rc.executePreparedQueryForUpdateOrDelete(q)
				}
				case None =>
			}
			Future(Ok("success"))
		})
	}

	case class ProveMemberPostShape(username: String)

	object ProveMemberPostShape{
		implicit val format = Json.format[ProveMemberPostShape]

		def apply(v: JsValue): ProveMemberPostShape = v.as[ProveMemberPostShape]
	}
}
