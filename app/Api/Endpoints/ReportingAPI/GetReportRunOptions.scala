package Api.Endpoints.ReportingAPI

import javax.inject.Inject

import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactoryInt
import Reporting.{Report, ReportFactory}
import Services.{CacheBroker, PersistenceBroker}
import Storable.StorableClass
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{JsArray, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class GetReportRunOptions @Inject()(lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action {
    // Tuples of (displayName, keyCode)
    val resultData: JsArray = Report.reportFactoryMap.foldLeft(new JsArray)((arr, e) => {
      val entityName: String = e._1
      val entityDisplayName: String = e._2._1

      val factoryInstance: ReportFactory[_ <: StorableClass] =
        Class.forName(e._2._2.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]

      // Field name and display name
      val fieldData: List[(String, String)] = factoryInstance.FIELD_MAP.map(f => (f._1, f._2.fieldDisplayName)).toList
      val filterData: List[(String, String, String)] = factoryInstance.FILTER_MAP.map(f => (f._1, f._2.displayName, f._2 match {
        case _: ReportingFilterFactoryInt[_] => "Int"
        case _ => throw new Exception("Unconfigured filter factory type for " + f._1)
      })).toList

      arr append JsObject(Map(
        "entityName" -> JsString(entityName),
        "displayName" -> JsString(entityDisplayName),
        "fieldData" -> fieldData.foldLeft(new JsArray)((arr, t) => {
         arr append JsObject(Map(
           "fieldName" -> JsString(t._1),
           "fieldDisplayName" -> JsString(t._2)
          ))
        }),
        "filterData" -> filterData.foldLeft(new JsArray)((arr, t) => {
          arr append JsObject(Map(
            "filterName" -> JsString(t._1),
            "displayName" -> JsString(t._2),
            "filterType" -> JsString(t._3)
          ))
        })
      ))
    })
    println(resultData)


    Ok(resultData).as("application/json")
  }
}