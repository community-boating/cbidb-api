package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.JpClassInstance
import org.sailcbi.APIServer.IO.JP.{AllJPClassInstances, AllJpClassSignups}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetJPClassSignups @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val instances = AllJPClassInstances.get(rc).map(Function.tupled((instance, start, end) => JpClassInstanceDecorated(
				jpClassInstance = instance, firstSession = start, lastSession = end, week = 2
			)))
			val signups = AllJpClassSignups.get(rc, instances.map(t => t.jpClassInstance.values.instanceId.get))
			implicit val writes = Json.writes[JpClassInstanceDecorated]
			val result = Json.toJson(Map(
				"instances" -> Json.toJson(instances),
				"signups" -> Json.toJson(signups),
			))
			Future(Ok(Json.toJson(result)))
		})
	})

	case class JpClassInstanceDecorated(
		jpClassInstance: JpClassInstance,
		firstSession: LocalDateTime,
		lastSession: LocalDateTime,
		week: Int
	)
}
