package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache, UnlockedRequestCache}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutUser @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutUserShape.apply)(parsed => {
			import org.sailcbi.APIServer.CbiUtil.JsValueWrapper.wrapJsValue
			request.body.asJson.map(json => json.getNonNull("userId")).get match {
				case Some(id: JsValue) => {

					val userId: Int = id.toString().toInt
					println(s"its an update: $userId")

					PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {


						println(parsed)
						runValidations(parsed, Some(userId)) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								// do update
								val user = rc.getObjectById(User, userId).get
								setValues(user, parsed)

								rc.commitObjectToDatabase(user)

								Future(Ok(new JsObject(Map(
									"userId" -> JsNumber(userId)
								))))
							}
						}
					})

				}
				case None => {
					println(s"its a create")
					PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {

						runValidations(parsed, None).combine(checkUsernameUnique(rc, parsed.username.get)) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								val user = new User
								setValues(user, parsed)
								user.update(_.userName, parsed.username.get)

								rc.commitObjectToDatabase(user)
								Future(Ok(new JsObject(Map(
									"userId" -> JsNumber(user.getID)
								))))
							}
						}
					})

				}
			}
		})
	}

	private def setValues(user: User, parsed: PutUserShape): Unit = {
		user.update(_.email, parsed.email.get)
		user.update(_.nameFirst, parsed.nameFirst)
		user.update(_.nameLast, parsed.nameLast)
		user.update(_.active, parsed.active.getOrElse(false))
		user.update(_.pwChangeRequired, parsed.pwChangeRequired.getOrElse(false))
		user.update(_.locked, parsed.locked.getOrElse(false))
		user.update(_.hideFromClose, parsed.hideFromClose.getOrElse(false))
		if (parsed.pwHash.isDefined) user.update(_.pwHash, parsed.pwHash)
	}

	private def runValidations(parsed: PutUserShape, userId: Option[Int]): ValidationResult = {
		ValidationResult.combine(List(
			ValidationResult.checkBlank(parsed.username, "Username"),
			ValidationResult.checkBlank(parsed.email, "Email"),
			ValidationResult.checkBlank(parsed.nameFirst, "First Name"),
			ValidationResult.checkBlank(parsed.nameLast, "Last Name"),
		))
	}

	private def checkUsernameUnique(rc: UnlockedRequestCache, candidate: String): ValidationResult = {
		val existingUsers = rc.countObjectsByFilters(User, List(User.fields.userName.equalsConstant(candidate)))

		if (existingUsers > 0) ValidationResult.from("That username is already in use")
		else ValidationOk
	}

	case class PutUserShape(
		userId: Option[Int],
		username: Option[String],
		email: Option[String],
		nameFirst: Option[String],
		nameLast: Option[String],
		active: Option[Boolean],
		hideFromClose: Option[Boolean],
		locked: Option[Boolean],
		pwChangeRequired: Option[Boolean],
		pwHash: Option[String]
	)

	object PutUserShape {
		implicit val format = Json.format[PutUserShape]

		def apply(v: JsValue): PutUserShape = v.as[PutUserShape]
	}
}
