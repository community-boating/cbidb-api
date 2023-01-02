package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.API.{ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions.SignoutCrew
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SignoutCrewRest @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, SignoutCrew.constructFromJsValue)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				doPost(rc, parsed) match {
					case ValidationOk => Future(Ok(Json.toJson(parsed)))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
				}
			})
		})
	})

	def delete()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, SignoutCrew.constructFromJsValue)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				parsed.values.signoutId.peek match {
					case Some(id) => {
						rc.deleteObjectsById(SignoutCrew, List(id))
						Future(Ok(Json.toJson(parsed)))
					}
					case None => Future(Ok(ValidationResult.from("No ID found for delete").toResultError.asJsObject))
				}
			})
		})
	})

	private def doPost(rc: UnlockedRequestCache, crew: SignoutCrew): ValidationResult = {
		// TODO: validations
		rc.commitObjectToDatabase(crew)
		ValidationOk
	}
}