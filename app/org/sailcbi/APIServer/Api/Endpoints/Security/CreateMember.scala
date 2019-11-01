package org.sailcbi.APIServer.Api.Endpoints.Security

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForInsert, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.ProtoPersonUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class CreateMember @Inject()(implicit exec: ExecutionContext) extends Controller {
	def post()(implicit PA: PermissionsAuthority) = Action { request =>
		try {
			val parsedRequest = ParsedRequest(request)
			val rc: RequestCache = PA.getRequestCache(ProtoPersonUserType, None, parsedRequest)._2.get
			println(parsedRequest)
			parsedRequest.postJSON match {
				case Some(v: JsValue) => {
					val cms = CreateMemberShape.apply(v)
					val protoPersonCookieValMaybe = parsedRequest.cookies.find(_.name == ProtoPersonUserType.COOKIE_NAME).map(_.value)
					println(cms)
					println(protoPersonCookieValMaybe)
					createMember(
						rc.pb,
						firstName = cms.firstName,
						lastName = cms.lastName,
						username = cms.username,
						pwHash = cms.pwHash,
						protoPersonValue = protoPersonCookieValMaybe
					) match {
						case Left(v: ValidationResult) => Ok(v.toResultError.asJsObject())
						case Right(id: Int) => Ok(new JsObject(Map(
							"personId" -> JsNumber(id)
						)))
					}
				}
				case None => Ok("Internal Error")
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
		pb: PersistenceBroker,
		firstName: String,
		lastName: String,
		username: String,
		pwHash: String,
		protoPersonValue: Option[String]
	): ValidationResult = {
		val inUse = () => ValidationResult.inline(username)(email => {
			val q = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType)) {
				override val params: List[String] = List(email.toLowerCase)

				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

				override def getQuery: String =
					s"""
					  |select person_id from persons where pw_hash is not null and lower(email) = ?
					  |""".stripMargin
			}
			pb.executePreparedQueryForSelect(q).isEmpty
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
			pb: PersistenceBroker,
			firstName: String,
			lastName: String,
			username: String,
			pwHash: String,
			protoPersonValue: Option[String]
	): Either[ValidationError, Int] = {
		createMemberValidations(pb, firstName, lastName, username, pwHash, protoPersonValue) match {
			case ve: ValidationError => Left(ve)
			case ValidationOk => getProtoPersonID(pb, protoPersonValue) match {
				case None => {
					val insertQ = new PreparedQueryForInsert(Set(ProtoPersonUserType)) {
						override val params: List[String] = List(
							firstName,
							lastName,
							username.toLowerCase(),
							pwHash
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
							  |    pw_hash
							  |  ) values (
							  |    ?,
							  |    ?,
							  |    ?,
							  |    'P',
							  |    '${MagicIds.PERSON_TYPE.JP_PARENT}',
							  |    ?
							  |  )
							  |""".stripMargin
					}
					val newPersonId = pb.executePreparedQueryForInsert(insertQ).get.toInt
					Right(newPersonId)
				}
				case Some(protoId: Int) => {
					val updateQ = new PreparedQueryForUpdateOrDelete(Set(ProtoPersonUserType)) {
						override val params: List[String] = List(
							firstName,
							lastName,
							username.toLowerCase(),
							pwHash,
							protoId.toString
						)

						override def getQuery: String =
							s"""
							  |update persons set
							  |proto_state = '${MagicIds.PERSONS_PROTO_STATE.WAS_PROTO}',
							  |name_first = ?,
							  |name_last = ?,
							  |email = ?,
							  |pw_hash = ?
							  |where proto_state = '${MagicIds.PERSONS_PROTO_STATE.IS_PROTO}'
							  |and person_id = ?
							  |""".stripMargin
					}
					pb.executePreparedQueryForUpdateOrDelete(updateQ)
					Right(protoId)
				}
			}
		}
	}

	private def getProtoPersonID(pb: PersistenceBroker, protoPersonValue: Option[String]): Option[Int] = {
		protoPersonValue match {
			case None => None
			case Some(v) => {
				val q = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType)) {
					override val params: List[String] = List(v)

					override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

					override def getQuery: String =
						s"""
						  |select person_id from persons where PROTO_STATE = '${MagicIds.PERSONS_PROTO_STATE.IS_PROTO}' and PROTOPERSON_COOKIE = ?
						  |""".stripMargin
				}
				val results = pb.executePreparedQueryForSelect(q)
				if (results.length == 1) Some(results.head)
				else None
			}
		}
	}
}
