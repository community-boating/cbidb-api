package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.framework.API.{RestController, ValidationResult}
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.framework.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUsers @Inject()(implicit val exec: ExecutionContext) extends RestController(User) with InjectedController {
	val fieldShutter: Set[DatabaseField[_]] = Set(
		User.fields.userId,
		User.fields.userName,
		User.fields.nameLast,
		User.fields.nameFirst,
		User.fields.email,
		User.fields.locked,
		User.fields.pwChangeRequired,
		User.fields.active,
		User.fields.hideFromClose,
		User.fields.userType
	)

	def getOneUser(userId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			getOne(rc, userId, fieldShutter) match {
				case Some(u) => Future(Ok(Json.toJson(u)))
				case None => Future(Ok(ValidationResult.from("No user found").toResultError.asJsObject()))
			}
		})
	})

	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val users = getByFilters(rc, List.empty, fieldShutter)
			Future(Ok(Json.toJson(users)))
		})
	})
}