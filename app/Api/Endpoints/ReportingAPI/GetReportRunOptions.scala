package Api.Endpoints.ReportingAPI

import javax.inject.Inject

import Reporting.{Report, ReportFactory}
import Services.{CacheBroker, PersistenceBroker}
import Storable.StorableClass
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{JsNull, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class GetReportRunOptions @Inject()(lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {
    // Tuples of (displayName, keyCode)
    val resultData: JsObject = Report.reportFactoryMap.foldLeft(new JsObject(Map()))((m, e) => {
      val entityName: String = e._1
      val entityDisplayName: String = e._2._1

      val factoryInstance: ReportFactory[_ <: StorableClass] =
        Class.forName(e._2._2.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]

      // Field name and display name
      val fieldData: List[(String, String)] = factoryInstance.FIELD_MAP.map(f => (f._1, f._2.fieldDisplayName)).toList
      val filterData: List[(String, String)] = factoryInstance.FILTER_MAP.map(f => (f._1, f._2.displayName)).toList
      m ++ JsObject(Map(
        entityName -> JsObject(Map(
          "displayName" -> JsString(entityDisplayName),
          "fieldData" -> fieldData.foldLeft(new JsObject(Map()))((m, t) => {
            m ++ JsObject(Map(
              t._1 -> JsString(t._2)
            ))
          }),
          "filterData" -> filterData.foldLeft(new JsObject(Map()))((m, t) => {
            m ++ JsObject(Map(
              t._1 -> JsString(t._2)
            ))
          })
        ))
      ))
    })
    println(resultData)


    Ok(resultData).as("application/json")
  }
}

/**/