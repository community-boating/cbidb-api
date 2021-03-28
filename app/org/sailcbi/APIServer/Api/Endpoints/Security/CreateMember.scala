package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.{PermissionsAuthority, RequestCache}
import com.coleji.framework.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.ProtoPersonRequestCache
import org.sailcbi.APIServer.Services.ResultSetWrapper
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CreateMember @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
			PA.withParsedPostBodyJSON(parsedRequest.postJSON, CreateMemberShape.apply)(cms => {
				val protoPersonCookieValMaybe = parsedRequest.cookies.find(_.name == ProtoPersonRequestCache.COOKIE_NAME).map(_.value)
				createMember(
					rc,
					firstName = cms.firstName,
					lastName = cms.lastName,
					username = cms.username,
					pwHash = cms.pwHash,
					protoPersonValue = protoPersonCookieValMaybe
				) match {
					case Left(v: ValidationResult) => Future(Ok(v.toResultError.asJsObject()))
					case Right(id: Int) => Future(Ok(new JsObject(Map(
						"personId" -> JsNumber(id)
					))))
				}
			})
		})
	}

	case class CreateMemberShape(
		username: String,
		firstName: String,
		lastName: String,
		pwHash: String
	)

	object CreateMemberShape {
		implicit val format = Json.format[CreateMemberShape]

		def apply(v: JsValue): CreateMemberShape = v.as[CreateMemberShape]
	}

	def createMemberValidations(
		rc: RequestCache,
		firstName: String,
		lastName: String,
		username: String,
		pwHash: String,
		protoPersonValue: Option[String]
	): ValidationResult = {
		val inUse = () => ValidationResult.inline(username)(email => {
			val q = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache)) {
				override val params: List[String] = List(email.toLowerCase)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override def getQuery: String =
					s"""
					  |select person_id from persons where pw_hash is not null and lower(email) = ?
					  |""".stripMargin
			}
			rc.executePreparedQueryForSelect(q).isEmpty
		}, "There is already an account for that email address.")

		ValidationResult.combine(List(
			ValidationResult.inline(firstName)(s => s != null && s.length > 0, "First Name must be specified."),
			ValidationResult.inline(lastName)(s => s != null && s.length > 0, "Last Name must be specified."),
			// email not null, then valid, then not in use
			(for {
				_ <- ValidationResult.inline(username)(s => s != null && s.length > 0, "Email must be specified.")
				_ <- ValidationResult.inline(username)(
					s => "(?i)^[A-Z0-9._%-]+@[A-Z0-9._%-]+\\.[A-Z]{2,4}$".r.findFirstMatchIn(s).isDefined,
					"Email is not valid."
				)
				x <- inUse()
			} yield x)
		))
	}

	def createMember(
			rc: RequestCache,
			firstName: String,
			lastName: String,
			username: String,
			pwHash: String,
			protoPersonValue: Option[String]
	): Either[ValidationError, Int] = {
		createMemberValidations(rc, firstName, lastName, username, pwHash, protoPersonValue) match {
			case ve: ValidationError => Left(ve)
			case ValidationOk => getProtoPersonID(rc, protoPersonValue) match {
				case None => {
					val insertQ = new PreparedQueryForInsert(Set(ProtoPersonRequestCache)) {
						override val params: List[String] = List(
							firstName,
							lastName,
							username.toLowerCase(),
							pwHash,
							MagicIds.PW_HASH_SCHEME.MEMBER_2
						)
						override val pkName: Option[String] = Some("PERSON_ID")

						override def getQuery: String =
							s"""
							  |insert into persons(
							  |    name_first,
							  |    name_last,
							  |    email,
							  |    temp,
							  |    person_type,
							  |    pw_hash,
							  |    pw_hash_scheme
							  |  ) values (
							  |    ?,
							  |    ?,
							  |    ?,
							  |    'P',
							  |    '${MagicIds.PERSON_TYPE.JP_PARENT}',
							  |    ?,
							  |    ?
							  |  )
							  |""".stripMargin
					}
					val newPersonId = rc.executePreparedQueryForInsert(insertQ).get.toInt
					Right(newPersonId)
				}
				case Some(protoId: Int) => {
					val updateQ = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonRequestCache)) {
						override val params: List[String] = List(
							firstName,
							lastName,
							username.toLowerCase(),
							pwHash,
							MagicIds.PW_HASH_SCHEME.MEMBER_2,
							protoId.toString
						)

						override def getQuery: String =
							s"""
							  |update persons set
							  |proto_state = '${MagicIds.PERSONS_PROTO_STATE.WAS_PROTO}',
							  |name_first = ?,
							  |name_last = ?,
							  |email = ?,
							  |pw_hash = ?,
							  |pw_hash_scheme = ?
							  |where proto_state = '${MagicIds.PERSONS_PROTO_STATE.IS_PROTO}'
							  |and person_id = ?
							  |""".stripMargin
					}
					rc.executePreparedQueryForUpdateOrDelete(updateQ)
					Right(protoId)
				}
			}
		}
	}

	private def getProtoPersonID(rc: RequestCache, protoPersonValue: Option[String]): Option[Int] = {
		protoPersonValue match {
			case None => None
			case Some(v) => {
				val q = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache)) {
					override val params: List[String] = List(v)

					override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

					override def getQuery: String =
						s"""
						  |select person_id from persons where PROTO_STATE = '${MagicIds.PERSONS_PROTO_STATE.IS_PROTO}' and PROTOPERSON_COOKIE = ?
						  |""".stripMargin
				}
				val results = rc.executePreparedQueryForSelect(q)
				if (results.length == 1) Some(results.head)
				else None
			}
		}
	}
}
