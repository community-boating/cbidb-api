package org.sailcbi.APIServer.Api.Endpoints.Staff

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Services.Authentication.StaffUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

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

					PA.withRequestCache(StaffUserType, None, parsedRequest, rc => {
						val pb = rc.pb

						runValidations(parsed, pb, Some(userId)) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								// do update
								val user = pb.getObjectById(User, userId).get
								user.update(_.email, parsed.email.get)
								user.update(_.nameFirst, parsed.nameFirst)
								user.update(_.nameLast, parsed.nameLast)

								pb.commitObjectToDatabase(user)

								Future(Ok(new JsObject(Map(
									"userId" -> JsNumber(userId)
								))))
							}
						}
					})

				}
				case None => {
					println(s"its a create")
					PA.withRequestCache(StaffUserType, None, parsedRequest, rc => {
						val pb = rc.pb
						runValidations(parsed, pb, None) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								val newJuniorId = -1  // do create
								Future(Ok(new JsObject(Map(
									"userId" -> JsNumber(newJuniorId)
								))))
							}
						}
					})

				}
			}
		})
	}

	def runValidations(parsed: PutUserShape, pb: PersistenceBroker, userId: Option[Int]): ValidationResult = {
		ValidationResult.combine(List(
			ValidationResult.checkBlank(parsed.userName, "Username"),
			ValidationResult.checkBlank(parsed.email, "Email"),
			ValidationResult.checkBlank(parsed.nameFirst, "First Name"),
			ValidationResult.checkBlank(parsed.nameLast, "Last Name"),
		))
	}

	case class PutUserShape(
		userId: Option[Int],
		userName: Option[String],
		email: Option[String],
		nameFirst: Option[String],
		nameLast: Option[String]
	)

	object PutUserShape {
		implicit val format = Json.format[PutUserShape]

		def apply(v: JsValue): PutUserShape = v.as[PutUserShape]
	}
}
