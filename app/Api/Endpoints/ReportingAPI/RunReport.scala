package Api.Endpoints.ReportingAPI

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.GetPostParams
import Reporting.Report
import Services.Authentication.StaffUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class RunReport @Inject() (implicit exec: ExecutionContext) extends Controller {
  object OUTPUT_TYPE {
    val JSCON = "jscon"
    val TSV = "tsv"
  }

  val errorResult = JsObject(Map("data" -> JsString("error")))

  def post(): Action[AnyContent] = Action.async {request =>
    try {
      val rc: RequestCache = PermissionsAuthority.spawnRequestCache(StaffUserType, request)
      val pb: PersistenceBroker = rc.pb
      val cb: CacheBroker = rc.cb
      // TODO: assert expected post params
      GetPostParams(request) match {
        case None => Future{ new Status(400)("no body") }
        case Some(params) => {
          val baseEntityString = params.get("baseEntityString").get
          val filterSpec = params.get("filterSpec").get
          val fieldSpec = params.get("fieldSpec").get
          val outputType = params.get("outputType").get
          println("Running a report with the following parameters: ")
          println("Base entity: " + baseEntityString)
          println("filter spec: " + filterSpec)
          println("field spec: " + fieldSpec)
          println("output type: " + outputType)
          lazy val apiRequest = new ReportRequest(pb, cb, baseEntityString, filterSpec, fieldSpec, outputType)
          outputType match {
            case OUTPUT_TYPE.JSCON => apiRequest.getFuture.map(s => Ok(s).as("application/json"))
            case OUTPUT_TYPE.TSV => Future {
              val reportResult: String = apiRequest.report.formatTSV
              val source: Source[ByteString, _] = Source.single(ByteString(reportResult))
              Result(
                header = ResponseHeader(200, Map(
                  CONTENT_DISPOSITION -> "attachment; filename=report.tsv"
                )),
                body = HttpEntity.Streamed(source, Some(reportResult.length), Some("application/text"))
              )
            }
            case _ => Future{Ok(errorResult).as("application/json")}
          }
        }
      }
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

  class ReportRequest(pb: PersistenceBroker, cb: CacheBroker, baseEntityString: String, filterSpec: String, fieldSpec: String, outputType: String) extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "report_" + baseEntityString + "_" + filterSpec + "_" + fieldSpec + "_" + outputType

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(5)
    }

    object params {}

    lazy val report: Report = Report.getReport(pb, baseEntityString, filterSpec, fieldSpec)

    def getJSONResultFuture: Future[JsObject] = Future {
      outputType match {
        case OUTPUT_TYPE.JSCON => report.formatJSCON
        case OUTPUT_TYPE.TSV => JsObject(Map(
          "tsv" -> JsString(report.formatTSV)
        ))
        case _ => errorResult
      }
    }
  }
}