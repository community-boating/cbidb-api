package Api.Endpoints.ReportingAPI

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.{JsonUtil, Profiler}
import Entities.{JpTeam, JpTeamEventPoints}
import Reporting.Report
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class RunReport @Inject()(lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def getTest(): Action[AnyContent] = get("ApClassType", "", "TypeName,TypeId")

  def get(baseEntityString: String, filterSpec: String, fieldSpec: String): Action[AnyContent] = Action.async {
    //val fieldSpec: String = "TypeName,TypeId" //,InstanceId,SessionCt,TypeDisplayOrder,FirstSessionDatetime"
    val request = new ReportRequest(baseEntityString, filterSpec, fieldSpec)
    request.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class ReportRequest(baseEntityString: String, filterSpec: String, fieldSpec: String) extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "report_" + baseEntityString + "_" + filterSpec + "_" + fieldSpec

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(5)
    }

    object params {}

    def getJSONResultFuture: Future[JsObject] = Future {
      Report.getReport(pb, baseEntityString, filterSpec, fieldSpec).formatJSCON
    }
  }
}



/* val source: Source[ByteString, _] = Source.single(ByteString(reportResult))
 Result(
   header = ResponseHeader(200, Map(
     CONTENT_DISPOSITION -> "attachment; filename=report.tsv"
   )),
   body = HttpEntity.Streamed(source, Some(reportResult.length), Some("application/text"))
 )*/
/*


val parser: ReportingFilterSpecParser[_ <: StorableClass] = Reporting.Report.BASE_ENTITY_MAP(baseEntityString)(pb)

val instances: Set[ApClassInstance] =
  parser.parse()
    .instances
    .asInstanceOf[Set[ApClassInstance]]



println("@#@#@# " + instances.size)



val result: String = new Reporting.Report[ApClassInstance](instances, fields).getReport(pb)



*/
