package org.sailcbi.APIServer.Api.Endpoints.Staff

import org.sailcbi.APIServer.Api.ValidationResult
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Services.Authentication.StaffRequestCache
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache}
import org.sailcbi.APIServer.Storable.Filter
import org.sailcbi.APIServer.Storable.StorableQuery.{QueryBuilder, TableAlias}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUsers @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	private def get(userId: Option[Int], theRC: RequestCache[_]): List[UserShape] = {
		implicit val rc: RequestCache[_] = theRC

		val users = {
			val users = TableAlias(User)
			val filter = userId match {
				case Some(u) => User.fields.userId.equalsConstant(u)(users.name)
				case None => Filter.noFilter
			}
			val qb = QueryBuilder
				.from(users)
				.where(filter)
				.select(List(
					User.fields.userId,
					User.fields.userName,
					User.fields.nameLast,
					User.fields.nameFirst,
					User.fields.email,
					User.fields.locked,
					User.fields.pwChangeRequired,
					User.fields.active,
					User.fields.hideFromClose
				).map(_.alias(users)))
			rc.executeQueryBuilder(qb).map(User.construct)
		}
		users.map(u => UserShape(
			userId = u.values.userId.get,
			username = u.values.userName.get,
			nameFirst = u.values.nameFirst.get,
			nameLast = u.values.nameLast.get,
			email = u.values.email.get,
			locked = u.values.locked.get,
			pwChangeRequired = u.values.pwChangeRequired.get,
			active = u.values.active.get,
			hideFromClose = u.values.hideFromClose.get
		))
	}

	def getOne(userId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val logger = PA.logger
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			get(Some(userId), rc) match {
				case l :: _ => {
					implicit val format = UserShape.format
					Future(Ok(Json.toJson(l)))
				}
				case _ => Future(Ok(ValidationResult.from("No user found").toResultError.asJsObject()))
			}

		})
	})

	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val logger = PA.logger
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val users = get(None, rc)
			implicit val format = UserShape.format
			Future(Ok(Json.toJson(users)))
		})
	})

	case class UserShape (
		userId: Int,
		username: String,
		nameFirst: Option[String],
		nameLast: Option[String],
		email: String,
		locked: Boolean,
		pwChangeRequired: Boolean,
		active: Boolean,
		hideFromClose: Boolean
	)

	object UserShape {
		implicit val format = Json.format[UserShape]
		def apply(v: JsValue): UserShape = v.as[UserShape]
	}
}