package Api.Endpoints.ReportingAPI

import java.time.LocalDateTime
import javax.inject.Inject

import Api.{ApiDataObject, AuthenticatedRequest, CacheableResult, ParamsObject}
import Api.Endpoints.ReportingAPI.GetReportRunOptions.{GetReportRunOptionsParamsObject, GetReportRunOptionsResult}
import Api.Endpoints.ReportingAPI.RunReport.RunReportParamsObject
import CbiUtil.ParsedRequest
import Reporting.Report
import Services.Authentication.StaffUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.{HeaderNames, HttpEntity}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class RunReport @Inject() (implicit val exec: ExecutionContext)
extends AuthenticatedRequest with CacheableResult[RunReportParamsObject, GetReportRunOptionsResult] {
  object OUTPUT_TYPE {
    val JSCON = "jscon"
    val TSV = "tsv"
  }

  val errorResult = JsObject(Map("data" -> JsString("error")))

  def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    val rc: RequestCache = PermissionsAuthority.getRequestCache(req.headers, req.cookies)
    println(rc.authenticatedUserType)
    if (rc.authenticatedUserType != StaffUserType) {
      Future{ Ok("Access Denied") }
    } else {
      try {
        val pb: PersistenceBroker = rc.pb
        val cb: CacheBroker = rc.cb
        // TODO: assert expected post params
        if (req.postParams.isEmpty) Future { Status(400)("no body")}
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
          getFuture(cb, pb, params, getJSONResultFuture(rc, params)).map(s => {
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
  }


  def getCacheBrokerKey(params: RunReportParamsObject): CacheKey
    = "report_" + params.baseEntityString + "_" + params.filterSpec + "_" + params.fieldSpec + "_" + params.outputType

  def getExpirationTime: LocalDateTime = {
    LocalDateTime.now.plusSeconds(5)
  }

  def getJSONResultFuture(rc: RequestCache, params: RunReportParamsObject): (() => Future[JsObject]) = () => Future {
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

  class RunReportsResult extends ApiDataObject
}