package Api.Endpoints

import javax.inject.Inject

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Reporting.ReportingFields.ApClassInstance.ApClassInstanceReportingFieldSessionCount
import Reporting.ReportingFields.ReportingField
import Reporting.ReportingFilters._
import Services.{CacheBroker, PersistenceBroker}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.inject.ApplicationLifecycle
import play.api.mvc._
import Storable.StorableClass

import scala.concurrent.ExecutionContext

class Report @Inject() (lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {
    val baseEntityString: String = "ApClassInstance"

    val parser: ReportingFilterSpecParser[_ <: StorableClass] = Reporting.Report.BASE_ENTITY_MAP(baseEntityString)(pb)

    val instances: Set[ApClassInstance] =
      parser.parse("ApClassInstanceFilterYear:2017%(ApClassInstanceFilterType:7|ApClassInstanceFilterType:8)")
        .instances
        .asInstanceOf[Set[ApClassInstance]]

    instances.foreach(i => {
      pb.getObjectById(ApClassFormat, i.values.formatId.get) match {
        case Some(f: ApClassFormat) => i.setApClassFormat(f)
      }
      val format: ApClassFormat = i.references.apClassFormat.get
      pb.getObjectById(ApClassType, format.values.typeId.get) match {
        case Some(t: ApClassType) => format.setApClassType(t)
      }
    })

    println("@#@#@# " + instances.size)

    val fields: List[ReportingField[ApClassInstance]] = List(
      ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassFormat](
        ApClassFormat.fields.typeId,
        i => i.references.apClassFormat.get,
        "Type ID"
      ),
      ReportingField.getReportingFieldFromDatabaseField(ApClassInstance.fields.instanceId, "Instance ID"),
      new ApClassInstanceReportingFieldSessionCount("Session Ct")
    )

    val result: String = new Reporting.Report[ApClassInstance](instances, fields).getReport(pb)

    val source: Source[ByteString, _] = Source.single(ByteString(result))

    Result(
      header = ResponseHeader(200, Map(
        CONTENT_DISPOSITION -> "attachment; filename=report.tsv"
      )),
      body = HttpEntity.Streamed(source, Some(result.length), Some("application/text"))
    )
  }
}
