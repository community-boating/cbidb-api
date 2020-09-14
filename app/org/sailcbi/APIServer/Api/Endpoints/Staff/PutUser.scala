package org.sailcbi.APIServer.Api.Endpoints.Staff

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{ValidationError, ValidationOk, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
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
		ValidationOk
	}

	case class PutUserShape(
		userId: Option[Int],
		userName: Option[String]
	)

	object PutUserShape {
		implicit val format = Json.format[PutUserShape]

		def apply(v: JsValue): PutUserShape = v.as[PutUserShape]
	}
}
