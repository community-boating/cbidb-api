package org.sailcbi.APIServer.Api.Endpoints.ReportingAPI

import org.sailcbi.APIServer.Api.Endpoints.ReportingAPI.GetReportRunOptions.{GetReportRunOptionsParamsObject, GetReportRunOptionsResult}
import org.sailcbi.APIServer.Api.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Reporting.{Report, ReportFactory}
import org.sailcbi.APIServer.Services.Authentication.StaffUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache}
import org.sailcbi.APIServer.Storable.StorableClass
import play.api.libs.json.{JsArray, JsBoolean, JsObject, JsString}
import play.api.mvc.{Action, AnyContent}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetReportRunOptions @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[GetReportRunOptionsParamsObject, GetReportRunOptionsResult] {
	def getCacheBrokerKey(params: GetReportRunOptionsParamsObject): CacheKey = "report-run-options"

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}

	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		PA.withRequestCache(StaffUserType)(None, ParsedRequest(request), rc => {

			val params = new GetReportRunOptionsParamsObject
			getFuture(rc.cb, rc, params, getJSONResultFuture(rc)).map(s => {
				Ok(s).as("application/json")
			})
		})
	}

	def getJSONResultFuture(rc: RequestCache[_]): (() => Future[JsObject]) = () => Future {
		case class FilterDataForJSON(
			filterName: String,
			displayName: String,
			filterType: String,
			defaultValue: String,
			dropdownValues: Option[List[List[(String, String)]]]
		)
		val resultData: JsArray = Report.reportFactoryMap.foldLeft(new JsArray)((arr, e) => {
			val entityName: String = e._1
			val entityDisplayName: String = e._2._1

			val factoryInstance: ReportFactory[_ <: StorableClass] =
				Class.forName(e._2._2.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]

			// Field name and display name
			val fieldData: List[(String, String, Boolean)] = factoryInstance.fieldList.map(f => (f._1, f._2.fieldDisplayName, f._2.isDefault))
			val filterData: List[FilterDataForJSON] =
				factoryInstance.filterList.map(f => FilterDataForJSON(
					f._1,
					f._2.displayName,
					f._2.argDefinitions.map(_._1).map({
						case ARG_INT => "Int"
						case ARG_DOUBLE => "Double"
						case ARG_DATE => "Date"
						case ARG_DROPDOWN => "Dropdown"
						case t: Any => throw new Exception("Unconfigured arg type " + t)
					}).mkString(","),
					f._2.argDefinitions.map(_._2).mkString(","),
					f._2 match {
						case d: ReportingFilterFactoryDropdown => Some(d.getDropdownValues(rc))
						case _ => None
					}
				)).toList

			arr append JsObject(Map(
				"entityName" -> JsString(entityName),
				"displayName" -> JsString(entityDisplayName),
				"fieldData" -> fieldData.foldLeft(new JsArray)((arr, t) => {
					arr append JsObject(Map(
						"fieldName" -> JsString(t._1),
						"fieldDisplayName" -> JsString(t._2),
						"isDefault" -> JsBoolean(t._3)
					))
				}),
				"filterData" -> filterData.foldLeft(new JsArray)((arr, t) => {
					arr append JsObject(Map(
						"filterName" -> JsString(t.filterName),
						"displayName" -> JsString(t.displayName),
						"filterType" -> JsString(t.filterType),
						"default" -> JsString(t.defaultValue),
						"values" -> (t.dropdownValues match {
							case Some(ll: List[List[(String, String)]]) => JsArray(ll.map(l => JsArray(l.map(v => JsObject(Map(
								"display" -> JsString(v._2),
								"return" -> JsString(v._1)
							))))))
							case _ => JsArray()
						})
					))
				})
			))
		})

		// return
		JsObject(Map(
			"runOptions" -> resultData
		))
	}
}


object GetReportRunOptions {

	class GetReportRunOptionsParamsObject extends ParamsObject

	class GetReportRunOptionsResult

}