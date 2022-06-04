package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.User

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Entities.access.CbiAccess
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
			PA.withRequestCache(StaffRequestCache, CbiAccess.permissions.PERM_GENERAL_ADMIN)(None, parsedRequest, rc => {
				put(rc, parsed) match {
					case Left(ve: ValidationError) => Future(Ok(ve.toResultError.asJsObject()))
					case Right(u: User) => Future(Ok(new JsObject(Map(
						"userId" -> JsNumber(u.values.userId.get)
					))))
				}
			})
		})
	}

	override protected def mutateDtoBeforeOperating(dto: PutUserDTO): PutUserDTO = {
		// Uppercase username
		PutUserDTO(
			USER_ID=dto.USER_ID,
			USER_NAME=dto.USER_NAME.toUpperCase,
			NAME_FIRST=dto.NAME_FIRST,
			NAME_LAST=dto.NAME_LAST,
			EMAIL=dto.EMAIL,
			ACTIVE=dto.ACTIVE,
			HIDE_FROM_CLOSE=dto.HIDE_FROM_CLOSE,
			LOCKED=dto.LOCKED,
			PW_CHANGE_REQD=dto.PW_CHANGE_REQD,
			USER_TYPE=dto.USER_TYPE,
			pwHash=dto.pwHash,
		)
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
		val existingUsers = rc.countObjectsByFilters(User, List(User.fields.userName.alias.equalsConstantLowercase(candidate.toLowerCase)))

		if (existingUsers > 0) ValidationResult.from("That username is already in use")
		else ValidationOk
	}
}
