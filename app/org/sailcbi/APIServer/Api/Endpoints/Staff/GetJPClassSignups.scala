package org.sailcbi.APIServer.Api.Endpoints.Staff

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.JP.{AllJPClassInstances, AllJpClassSignups}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetJPClassSignups @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val instances = AllJPClassInstances.get(rc)
			val signups = AllJpClassSignups.get(rc, instances.map(t => t._1.values.instanceId.get))

			val result = Json.toJson(Map(
				"instances" -> Json.toJson(instances),
				"signups" -> Json.toJson(signups),
			))
			Future(Ok(Json.toJson(result)))
		})
	})
}
