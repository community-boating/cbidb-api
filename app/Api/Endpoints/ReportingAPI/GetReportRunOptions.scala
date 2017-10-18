package Api.Endpoints.ReportingAPI

import javax.inject.Inject

import Reporting.{Report, ReportFactory}
import Services.{CacheBroker, PersistenceBroker}
import Storable.StorableClass
import oracle.net.aso.e
import play.api.inject.ApplicationLifecycle
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class GetReportRunOptions @Inject()(lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {
    // Tuples of (displayName, keyCode)
    val resultData = Report.reportFactoryMap.map(e => {
      val entityName: String = e._1
      val entityDisplayName: String = e._2._1

      val factoryInstance: ReportFactory[_ <: StorableClass] =
        Class.forName(e._2._2.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]

      // Field name and display name
      val fieldData: List[(String, String)] = factoryInstance.FIELD_MAP.map(f => (f._1, f._2.fieldDisplayName)).toList
      val filterData: List[(String, String)] = factoryInstance.FILTER_MAP.map(f => (f._1, f._2.displayName)).toList
      println(filterData)


      "dfgdfg"
    })


    Ok("dfgdfg")
  }
}