package org.sailcbi.APIServer.Api.Endpoints.Staff

import javax.inject.Inject
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassType, User}
import org.sailcbi.APIServer.Services.Authentication.StaffUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache}
import org.sailcbi.APIServer.Storable.StorableQuery.{QueryBuilder, TableAlias}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import scala.concurrent.{ExecutionContext, Future}

class GetUsers @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val logger = PA.logger
		PA.withRequestCache(StaffUserType, None, ParsedRequest(req), theRC => {
			implicit val rc: RequestCache = theRC
			val pb = rc.pb

			val users = {
				val users = TableAlias(User)
				val qb = QueryBuilder
					.from(users)
					.select(List(
						User.fields.userId.alias(users),
						User.fields.userName.alias(users)
					))
				pb.executeQueryBuilder(qb).map(User.construct)
			}
			implicit val format = UserShape.format
			Future(Ok(Json.toJson(users.map(u => UserShape(u.values.userId.get, u.values.userName.get)))))
		})
	})

	case class UserShape (
		userId: Int,
		userName: String
	)

	object UserShape {
		implicit val format = Json.format[UserShape]

		def apply(v: JsValue): UserShape = v.as[UserShape]
	}
}