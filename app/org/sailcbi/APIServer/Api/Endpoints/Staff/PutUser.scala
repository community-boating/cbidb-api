package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.framework.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Entities.dto.PutUserDTO
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutUser @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutUserDTO.apply)(parsed => {
			import com.coleji.framework.Util.JsValueWrapper.wrapJsValue
			request.body.asJson.map(json => json.getNonNull("USER_ID")).get match {
				case Some(id: JsValue) => {

					val userId: Int = id.toString().toInt
					println(s"its an update: $userId")

					PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
						val u = User.getAuthedUser(rc)
						if (u.values.userName.get != "JCOLE") {
							throw new Exception("Locked to jcole only")
						}


						println(parsed)
						runValidations(parsed, Some(userId)) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								// do update
								val user = rc.getObjectById(User, userId).get
								parsed.mutateStorable(user)

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
						val u = User.getAuthedUser(rc)
						if (u.values.userName.get != "JCOLE") {
							throw new Exception("Locked to jcole only")
						}

						runValidations(parsed, None).combine(checkUsernameUnique(rc, parsed.USER_NAME)) match {
							case ve: ValidationError => Future(Ok(ve.toResultError.asJsObject()))
							case ValidationOk => {
								val user = parsed.unpackage
								user.update(_.userName, parsed.USER_NAME)

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

	private def runValidations(parsed: PutUserDTO, userId: Option[Int]): ValidationResult = {
		ValidationResult.combine(List(
			ValidationResult.checkBlank(parsed.NAME_FIRST, "First Name"),
			ValidationResult.checkBlank(parsed.NAME_LAST, "Last Name"),
		))
	}

	private def checkUsernameUnique(rc: UnlockedRequestCache, candidate: String): ValidationResult = {
		val existingUsers = rc.countObjectsByFilters(User, List(User.fields.userName.equalsConstant(candidate)))

		if (existingUsers > 0) ValidationResult.from("That username is already in use")
		else ValidationOk
	}


}
