package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.DockReport

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{DockReport, DockReportApClass}
import org.sailcbi.APIServer.Entities.dto.PutDockReportApClassDto
import org.sailcbi.APIServer.Logic.DockhouseLogic.DockReportLogic
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RefreshDockReportClasses @Inject()(implicit val exec: ExecutionContext) extends InjectedController {

  def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
    val parsedRequest = ParsedRequest(req)
    PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
      Future(Ok(Json.toJson(rc.getObjectsByFilters(DockReport, List(DockReport.fields.reportDate.alias.isDateConstant(PA.now().toLocalDate)), Set(
        DockReport.fields.dockReportId
      )).headOption.map(dockReportToday => {
        val currentClasses = DockReportLogic.getDockReportClasses(rc, dockReportToday)

        DockReportLogic.refreshDockReportClasses(rc, dockReportToday, id => {
          currentClasses.find(c => {
            c.values.apInstanceId.get == id
          }).getOrElse(new DockReportApClass())

        })
      }).getOrElse(List[PutDockReportApClassDto]()))))
    })
  })

}
