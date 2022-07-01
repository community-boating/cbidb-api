package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.User

import com.coleji.neptune.API.{RestControllerWithDTO, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Entities.access.CbiAccess
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNumber, JsObject, Json}
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutUser @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, User.constructFromJsValue)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiAccess.permissions.PERM_GENERAL_ADMIN)(None, parsedRequest, rc => {
				// If the password is set as blank, unset
				parsed.values.pwHash.peek match {
					case Some(None) => parsed.values.pwHash.unset()
					case _ =>
				}

				// Set username to uppercase
				parsed.values.userName.update(parsed.values.userName.get.toUpperCase)

				val isUpdate = parsed.values.userId.isSet && rc.countObjectsByFilters(User, List(User.fields.userId.alias.equalsConstant(parsed.values.userId.get))) > 0

				if (isUpdate) {
					runValidationsForUpdate(rc, parsed) match {
						case ValidationOk => {
							rc.commitObjectToDatabase(parsed)
							Future(Ok(Json.toJson(parsed)))
						}
						case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					}
				} else {
					// TODO: remove this
					parsed.values.userType.update("U")
					runValidationsForInsert(rc, parsed) match {
						case ValidationOk => {
							rc.commitObjectToDatabase(parsed)
							Future(Ok(Json.toJson(parsed)))
						}
						case e: ValidationError => Future(Ok(e.toResultError.asJsObject()))
					}
				}
			})
		})
	}

	def runValidationsForUpdate(rc: UnlockedRequestCache, u: User): ValidationResult = {
		ValidationResult.combine(List(
			ValidationResult.checkBlank(u.values.nameFirst.get, "First Name"),
			ValidationResult.checkBlank(u.values.nameLast.get, "Last Name"),
		))
	}

	def runValidationsForInsert(rc: UnlockedRequestCache, u: User): ValidationResult =
		runValidationsForUpdate(rc, u).combine(checkUsernameUnique(rc, u.values.userName.get))

	private def checkUsernameUnique(rc: UnlockedRequestCache, candidate: String): ValidationResult = {
		val existingUsers = rc.countObjectsByFilters(User, List(User.fields.userName.alias.equalsConstantLowercase(candidate.toLowerCase)))

		if (existingUsers > 0) ValidationResult.from("That username is already in use")
		else ValidationOk
	}
}
