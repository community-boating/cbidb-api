package org.sailcbi.APIServer.Api.Endpoints.Public

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FlagColor @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache(PublicRequestCache)(None, ParsedRequest(request), rc => {
			val flagColor = org.sailcbi.APIServer.Entities.cacheable.FlagColor.get(rc, null)._1 match {
				case "G" => "G"
				case "Y" => "Y"
				case "R" => "R"
				case _ => "C"
			}
			Future(Ok("var FLAG_COLOR = \"" + flagColor + "\""))
		})
	}
}
