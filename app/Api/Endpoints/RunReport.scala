package Api.Endpoints

import javax.inject.Inject

import Reporting.Report
import Services.{CacheBroker, PersistenceBroker}
import play.api.inject.ApplicationLifecycle
import play.api.mvc._

import scala.concurrent.ExecutionContext

class RunReport @Inject()(lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {
    val baseEntityString: String = "ApClassInstance"
    val filterSpec: String = "ApClassInstanceFilterYear:2017%(ApClassInstanceFilterType:7|ApClassInstanceFilterType:8)"
    val fieldSpec: String = "InstanceId,TypeName"

    val reportResult: String = Report.getReport(pb, baseEntityString, filterSpec, fieldSpec)
    Ok(reportResult)

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
  }
}
