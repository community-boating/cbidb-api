package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.User

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Entities.dto.PutUserDTO
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutUser @Inject()(implicit exec: ExecutionContext) extends RestControllerWithDTO[User, PutUserDTO](User) with InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, PutUserDTO.apply)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject()))
					case Right(u: User) => Future(Ok(new JsObject(Map(
						"userId" -> JsNumber(u.values.userId.get)
					))))
				}
			})
		})
	}

	override def runValidationsForUpdate(rc: UnlockedRequestCache, d: PutUserDTO): ValidationResult = {
		ValidationResult.combine(List(
			ValidationResult.checkBlank(d.NAME_FIRST, "First Name"),
			ValidationResult.checkBlank(d.NAME_LAST, "Last Name"),
		))
	}

	override def runValidationsForInsert(rc: UnlockedRequestCache, d: PutUserDTO): ValidationResult =
		runValidationsForUpdate(rc, d).combine(checkUsernameUnique(rc, d.USER_NAME))

	private def checkUsernameUnique(rc: UnlockedRequestCache, candidate: String): ValidationResult = {
		val existingUsers = rc.countObjectsByFilters(User, List(User.fields.userName.equalsConstant(candidate)))

		if (existingUsers > 0) ValidationResult.from("That username is already in use")
		else ValidationOk
	}
}
