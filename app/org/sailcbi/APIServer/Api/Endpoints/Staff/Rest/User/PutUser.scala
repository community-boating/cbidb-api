package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.User

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{AccessProfileRelationship, User}
import org.sailcbi.APIServer.Entities.access.CbiPermissions
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.InjectedController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PutUser @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority) = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, User.constructFromJsValue)(parsed => {
			PA.withRequestCache(StaffRequestCache, CbiPermissions.PERM_MANAGE_USERS_SCREEN)(None, parsedRequest, rc => {
				// If the password is set as blank, unset
				parsed.values.pwHash.peek match {
					case None => parsed.values.pwHash.unset()
					case _ =>
				}

				// Set username to uppercase
				parsed.values.userName.update(parsed.values.userName.get.toUpperCase)

				val isUpdate = parsed.values.userId.isSet && rc.countObjectsByFilters(User, List(User.fields.userId.alias.equalsConstant(parsed.values.userId.get))) > 0

				if (isUpdate) {
					val existingRecord = rc.getObjectsByFilters(User, List(User.fields.userId.alias.equalsConstant(parsed.values.userId.get)), Set(User.fields.accessProfileId)).head
					runValidationsForUpdate(rc, parsed, existingRecord) match {
						case ValidationOk => {
							rc.commitObjectToDatabase(parsed)
							Future(Ok(Json.toJson(parsed)))
						}
						case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
					}
				} else {
					// TODO: remove this
					runValidationsForInsert(rc, parsed) match {
						case ValidationOk => {
							rc.commitObjectToDatabase(parsed)
							Future(Ok(Json.toJson(parsed)))
						}
						case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
					}
				}
			})
		})
	}

	def runUnifiedValidations(rc: UnlockedRequestCache, newRecord: User): ValidationResult = {
		ValidationResult.combine(List(
			ValidationResult.checkBlank(newRecord.values.nameFirst.peek, "First Name"),
			ValidationResult.checkBlank(newRecord.values.nameLast.peek, "Last Name"),
		))
	}

	def runValidationsForUpdate(rc: UnlockedRequestCache, newRecord: User, existingRecord: User): ValidationResult = {
		runUnifiedValidations(rc, newRecord).combine(canUpdateUser(rc, existingRecord))
	}

	def runValidationsForInsert(rc: UnlockedRequestCache, u: User): ValidationResult = {
		ValidationResult.combine(List(
			runUnifiedValidations(rc, u),
			checkUsernameUnique(rc, u.values.userName.get),
			checkEmailUnique(rc, u.values.email.get)
		))
	}

	private def checkUsernameUnique(rc: UnlockedRequestCache, candidate: String): ValidationResult = {
		val existingUsers = rc.countObjectsByFilters(User, List(User.fields.userName.alias.equalsConstantLowercase(candidate.toLowerCase)))

		if (existingUsers > 0) ValidationResult.from("That username is already in use")
		else ValidationOk
	}

	private def checkEmailUnique(rc: UnlockedRequestCache, candidate: String): ValidationResult = {
		val existingUsers = rc.countObjectsByFilters(User, List(User.fields.email.alias.equalsConstantLowercase(candidate.toLowerCase)))

		if (existingUsers > 0) ValidationResult.from("That username is already in use")
		else ValidationOk
	}

	private def canUpdateUser(rc: UnlockedRequestCache, u: User): ValidationResult = {
		if (rc.hasPermission(CbiPermissions.PERM_MANAGE_ACCESS)) ValidationOk
		else {
			val updatingUserAccessProfile = rc.getObjectsByFilters(
				User,
				List(User.fields.userName.alias.equalsConstantLowercase(rc.userName.toLowerCase)),
				Set(User.fields.userId, User.fields.accessProfileId)
			).head.values.accessProfileId.get

			val accessProfilesRelationships = rc.assertUnlocked.getAllObjectsOfClass(AccessProfileRelationship, Set(
				AccessProfileRelationship.fields.managingProfileId,
				AccessProfileRelationship.fields.subordinateProfileId
			))

			val matchingApr = accessProfilesRelationships.exists(apr => apr.values.managingProfileId.get == updatingUserAccessProfile && apr.values.subordinateProfileId.get == u.values.accessProfileId.get)

			if (matchingApr) ValidationOk
			else ValidationResult.from("You do not have permission to modify a user of that access level.")
		}



	}
}
