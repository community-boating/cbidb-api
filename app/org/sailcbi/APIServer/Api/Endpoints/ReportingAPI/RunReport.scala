package org.sailcbi.APIServer.Api.Endpoints.ReportingAPI

import akka.stream.scaladsl.Source
import akka.util.ByteString
import org.sailcbi.APIServer.Api.Endpoints.ReportingAPI.GetReportRunOptions.GetReportRunOptionsResult
import org.sailcbi.APIServer.Api.Endpoints.ReportingAPI.RunReport.RunReportParamsObject
import org.sailcbi.APIServer.Api.{CacheableResultFromPreparedQuery, ParamsObject}
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Reporting.Report
import org.sailcbi.APIServer.Services.Authentication.StaffRequestCache
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, RequestCache}
import play.api.http.{HeaderNames, HttpEntity}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc._

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RunReport @Inject()(implicit val exec: ExecutionContext)
		extends CacheableResultFromPreparedQuery[RunReportParamsObject, GetReportRunOptionsResult] {

	object OUTPUT_TYPE {
		val JSCON = "jscon"
		val TSV = "tsv"
	}

	val errorResult = JsObject(Map("data" -> JsString("error")))

	def post(): Action[AnyContent] = Action.async { r => doPost(ParsedRequest(r)) }

	def doPost(req: ParsedRequest)(implicit PA: PermissionsAuthority): Future[Result] = {
		PA.withRequestCache(StaffRequestCache)(None, req, rc => {
			println(rc.auth.userName)
			if (!rc.auth.isInstanceOf[StaffRequestCache]) {
				Future {
					Ok("Access Denied")
				}
			} else {
				try {

					val cb: CacheBroker = rc.cb
					// TODO: assert expected post params
					if (req.postParams.isEmpty) Future {
						Status(400)("no body")
					}
					else {
						val params = RunReportParamsObject(
							req.postParams("baseEntityString"),
							req.postParams("filterSpec"),
							req.postParams("fieldSpec"),
							req.postParams("outputType")
						)
						println("Running a report with the following parameters: ")
						println("Base entity: " + params.baseEntityString)
						println("filter spec: " + params.filterSpec)
						println("field spec: " + params.fieldSpec)
						println("output type: " + params.outputType)
						getFuture(cb, rc, params, getJSONResultFuture(rc, params)).map(s => {
							println(s)
							params.outputType match {
								case OUTPUT_TYPE.JSCON => Ok(s).as("application/json")
								case OUTPUT_TYPE.TSV => {
									val json: JsValue = Json.parse(s)
									val result: String = json
											.asInstanceOf[JsObject]("data")
											.asInstanceOf[JsObject]("tsv")
											.asInstanceOf[JsString]
											.value
											.replace("\\t", "\t")
											.replace("\\n", "\n")
									val source: Source[ByteString, _] = Source.single(ByteString(result))
									Result(
										header = ResponseHeader(200, Map(
											HeaderNames.CONTENT_DISPOSITION -> "attachment; filename=report.tsv"
										)),
										body = HttpEntity.Streamed(source, Some(result.length), Some("application/text"))
									)
								}
								case _ => Ok(errorResult).as("application/json")
							}
						})
					}
				} catch {
					case _: UnauthorizedAccessException => Future {
						Ok("Access Denied")
					}
					case _: Throwable => Future {
						Ok("Internal Error")
					}
				}
			}
		})

	}


	def getCacheBrokerKey(params: RunReportParamsObject): CacheKey
	= "report_" + params.baseEntityString + "_" + params.filterSpec + "_" + params.fieldSpec + "_" + params.outputType

	def getExpirationTime: LocalDateTime = {
		LocalDateTime.now.plusSeconds(5)
	}

	def getJSONResultFuture(rc: RequestCache[_], params: RunReportParamsObject): (() => Future[JsObject]) = () => Future {
		lazy val report: Report = Report.getReport(rc, params.baseEntityString, params.filterSpec, params.fieldSpec)
		params.outputType match {
			case OUTPUT_TYPE.JSCON => report.formatJSCON
			case OUTPUT_TYPE.TSV => JsObject(Map(
				"tsv" -> JsString(report.formatTSV)
			))
			case _ => errorResult
		}
	}
}

object RunReport {

	case class RunReportParamsObject(
		baseEntityString: String,
		filterSpec: String,
		fieldSpec: String,
		outputType: String
	) extends ParamsObject

	class RunReportsResult

}
