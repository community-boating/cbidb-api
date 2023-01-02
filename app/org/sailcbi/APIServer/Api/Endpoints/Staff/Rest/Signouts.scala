package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.API.{RestController, ValidationError, ValidationOk, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Signout
import org.sailcbi.APIServer.Entities.cacheable.SignoutsToday
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class Signouts @Inject()(implicit val exec: ExecutionContext) extends RestController(Signout) with InjectedController {
	def getToday()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val (result, (created, expires)) = SignoutsToday.get(rc, null)
			Future(Ok(result))
		})
	})

	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, Signout.constructFromJsValue)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				doSignout(rc)(parsed) match {
					case ValidationOk => Future(Ok(Json.toJson(parsed)))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
				}
			})
		})
	})

	def postList()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withParsedPostBodyJSON(parsedRequest.postJSON, Signout.constructListFromJsArray)(parsed => {
			PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
				parsed.map(doSignout(rc)).reduce(ValidationResult.reduce) match {
					case ValidationOk => Future(Ok(Json.toJson(parsed)))
					case e: ValidationError => Future(Ok(e.toResultError.asJsObject))
				}
			})
		})
	})

	def doSignout(rc: StaffRequestCache)(s: Signout): ValidationResult = {
		val existingSignout = s.values.signoutId.peek.flatMap(rc.getObjectById(Signout, _, Set(Signout.fields.signoutId)))
		if (existingSignout.isDefined) {
			rc.commitObjectToDatabase(s)
			ValidationOk
		} else {
			ValidationResult.from("Unable to locate signout to update for id: " + s.values.signoutId.peek.map(_.toString).getOrElse("(unset)"))
		}
	}
}
