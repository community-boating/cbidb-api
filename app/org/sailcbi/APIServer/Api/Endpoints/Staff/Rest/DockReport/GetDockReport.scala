package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DockReport

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.DockReport
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNull, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDockReport @Inject()(implicit val exec: ExecutionContext) extends RestController(DockReport) with InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val dockReport = getByFilters(rc, List(DockReport.fields.reportDate.alias.isDateConstant(LocalDate.now)), Set.empty).head
			Future(Ok(Json.toJson(dockReport)))
		})
	})
}
