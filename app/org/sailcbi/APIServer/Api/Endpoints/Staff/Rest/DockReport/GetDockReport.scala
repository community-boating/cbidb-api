package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.DockReport

import com.coleji.neptune.API.RestController
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassFormat, ApClassInstance, ApClassSession, ApClassType, DockReport, DockReportApClass, DockReportHullCount, DockReportStaff, DockReportUapAppt, DockReportWeather}
import org.sailcbi.APIServer.Entities.dto.{PutDockReportApClassDto, PutDockReportDto, PutDockReportHullCountDto, PutDockReportStaffDto, PutDockReportUapApptDto, PutDockReportWeatherDto}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsNull, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDockReport @Inject()(implicit val exec: ExecutionContext) extends RestController(DockReport) with InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val dockReport = getByFilters(rc, List(DockReport.fields.reportDate.alias.isDateConstant(LocalDate.now)), Set.empty)
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
		getByFilters(rc, List(DockReport.fields.reportDate.alias.isDateConstant(LocalDate.now.minusDays(1))), Set.empty).headOption.foreach(yesterday => {
			newDockReport.values.semiPermanentRestrictions.update(yesterday.values.semiPermanentRestrictions.get)
		})

		rc.commitObjectToDatabase(newDockReport)

		println("created new dock report id " + newDockReport.values.dockReportId.get)

		// today's ap classes
		val apClassesQb = QueryBuilder
			.from(ApClassSession)
			.innerJoin(ApClassInstance, ApClassInstance.fields.instanceId.alias equalsField  ApClassSession.fields.instanceId.alias)
			.innerJoin(ApClassFormat, ApClassFormat.fields.formatId.alias equalsField ApClassInstance.fields.formatId.alias)
			.innerJoin(ApClassType, ApClassType.fields.typeId.alias equalsField ApClassFormat.fields.typeId.alias)
			.where(ApClassSession.fields.sessionDateTime.alias isDateConstant LocalDate.now())
			.select(List(
				ApClassType.fields.typeName.alias,
				ApClassInstance.fields.instanceId.alias,
				ApClassSession.fields.sessionDateTime.alias
			))

		val apClassesQbrrs = rc.executeQueryBuilder(apClassesQb)

		val dockReportClasses = apClassesQbrrs.map(qbrr => {
			val dockRptClass = new DockReportApClass
			dockRptClass.values.dockReportId.update(newDockReport.values.dockReportId.get)
			dockRptClass.values.className.update(qbrr.getValue(ApClassType.alias)(_.typeName))
			dockRptClass.values.classDatetime.update(qbrr.getValue(ApClassSession.alias)(_.sessionDateTime))
			dockRptClass.values.apInstanceId.update(Some(qbrr.getValue(ApClassInstance.alias)(_.instanceId)))
			rc.commitObjectToDatabase(dockRptClass)
			dockRptClass.defaultAllUnsetNullableFields()
			PutDockReportApClassDto(dockRptClass)
		})

		val dockReportDto = PutDockReportDto(newDockReport)
		dockReportDto.apClasses = dockReportClasses
		dockReportDto
	}
}
