package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.BoatType
import org.sailcbi.APIServer.Entities.cacheable.BoatTypes
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BoatTypes @Inject()(implicit val exec: ExecutionContext) extends RestController(BoatType) with InjectedController {
	def getAll()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			Future(Ok(BoatTypes.get(rc, null)._1))
		})
	})
}