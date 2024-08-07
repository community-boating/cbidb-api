package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DockReport

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.dto.{PutDockReportDto, PutDockReportHullCountDto}
import org.sailcbi.APIServer.Logic.DockhouseLogic.DockReportLogic.refreshDockReportClasses
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDockReport @Inject()(implicit val exec: ExecutionContext) extends RestController(DockReport) with InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val dockReport = getByFilters(rc, List(DockReport.fields.reportDate.alias.isDateConstant(PA.now().toLocalDate)), Set(
				DockReport.fields.dockReportId,
				DockReport.fields.reportDate,
				DockReport.fields.sunsetDatetime,
				DockReport.fields.incidentsNotes,
				DockReport.fields.announcements,
				DockReport.fields.semiPermanentRestrictions,
				DockReport.fields.signedBy,
			))
				.map(PutDockReportDto.applyWithSubObjects(rc)).headOption

			dockReport match {
				case Some(dr) => Future(Ok(Json.toJson(dockReport)))
				case None => {
					val newDockReport = createNewDockReport(rc)
					Future(Ok(Json.toJson(newDockReport)))
				}
			}

		})
	})

	private def createNewDockReport(rc: UnlockedRequestCache): PutDockReportDto = {
		val newDockReport = new DockReport
		newDockReport.values.reportDate.update(LocalDate.now)
		// copy yesterday's values
		val yesterday = getByFilters(rc, List(DockReport.fields.reportDate.alias.isDateConstant(LocalDate.now.minusDays(1))), Set(
			DockReport.fields.dockReportId,
			DockReport.fields.reportDate,
			DockReport.fields.sunsetDatetime,
			DockReport.fields.incidentsNotes,
			DockReport.fields.announcements,
			DockReport.fields.semiPermanentRestrictions,
			DockReport.fields.signedBy,
		)).headOption

		yesterday.foreach(yesterday => {
			newDockReport.values.semiPermanentRestrictions.update(yesterday.values.semiPermanentRestrictions.get)
			newDockReport.values.announcements.update(yesterday.values.announcements.get)
		})

		rc.commitObjectToDatabase(newDockReport)

		// today's ap classes

		val dockReportClasses = refreshDockReportClasses(rc, newDockReport, (num) => {
			new DockReportApClass()
		})

		// copy in svc hull counts
		val yesterdaysHullCts = yesterday.map(y => y.values.dockReportId.get).map(id => {
			rc.getObjectsByFilters(DockReportHullCount, List(DockReportHullCount.fields.dockReportId.alias.equalsConstant(id)), Set(
				DockReportHullCount.fields.dockReportId,
				DockReportHullCount.fields.dockReportHullCtId,
				DockReportHullCount.fields.hullType,
				DockReportHullCount.fields.inService,
				DockReportHullCount.fields.staffTally
			))
		}).getOrElse(List.empty)

		val newHullCts = yesterdaysHullCts.map(ct => {
			val newHullCt = new DockReportHullCount
			newHullCt.values.dockReportId.update(newDockReport.values.dockReportId.get)
			newHullCt.values.hullType.update(ct.values.hullType.get)
			newHullCt.values.inService.update(ct.values.inService.get)
			newHullCt.defaultAllUnsetNullableFields()
			rc.commitObjectToDatabase(newHullCt)
			newHullCt
		}).map(PutDockReportHullCountDto.apply)

		val dockReportDto = PutDockReportDto(newDockReport)
		dockReportDto.apClasses = dockReportClasses
		dockReportDto.hullCounts = newHullCts
		dockReportDto
	}
}
