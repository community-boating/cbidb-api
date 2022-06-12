package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.Signouts

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.Signout
import org.sailcbi.APIServer.Entities.cacheable.SignoutsToday
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetSignouts @Inject()(implicit val exec: ExecutionContext) extends RestController(Signout) with InjectedController {
	def getToday()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val (result, (created, expires)) = SignoutsToday.get(rc, null)
			Future(Ok(result))
		})
	})
}
