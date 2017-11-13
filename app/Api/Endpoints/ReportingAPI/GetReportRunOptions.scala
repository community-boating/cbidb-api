package Api.Endpoints.ReportingAPI

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import Reporting.ReportingFilters.ReportingFilterFactories.{ReportingFilterFactoryDropdown, ReportingFilterFactoryInt}
import Reporting.{Report, ReportFactory}
import Services.{CacheBroker, PersistenceBroker}
import Storable.StorableClass
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{JsArray, JsNull, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class GetReportRunOptions @Inject()(lifecycle: ApplicationLifecycle, cb: CacheBroker, pb: PersistenceBroker)(implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action.async {
    //val fieldSpec: String = "TypeName,TypeId" //,InstanceId,SessionCt,TypeDisplayOrder,FirstSessionDatetime"
    val request = new ReportRunOptionsRequest()
    request.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class ReportRunOptionsRequest() extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "reportRunOptions"

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusSeconds(5)
    }

    object params {}

    def getJSONResultFuture: Future[JsObject] = Future {
      case class FilterDataForJSON(
        filterName: String,
        displayName: String,
        filterType: String,
        defaultValue: String,
        dropdownValues: Option[List[(String, String)]]
      )
      val resultData: JsArray = Report.reportFactoryMap.foldLeft(new JsArray)((arr, e) => {
        val entityName: String = e._1
        val entityDisplayName: String = e._2._1

        val factoryInstance: ReportFactory[_ <: StorableClass] =
          Class.forName(e._2._2.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]

        // Field name and display name
        val fieldData: List[(String, String)] = factoryInstance.FIELD_MAP.map(f => (f._1, f._2.fieldDisplayName)).toList
        val filterData: List[FilterDataForJSON] =
          factoryInstance.FILTER_MAP.map(f => FilterDataForJSON(
            f._1,
            f._2.displayName,
            f._2 match {
                // TODO: right here it should be dropdown, not later
              case _: ReportingFilterFactoryInt[_] => "Int"
              case _ => throw new Exception("Unconfigured filter factory type for " + f._1)
            },
            f._2.defaultValue,
            f._2 match {
              case d: ReportingFilterFactoryDropdown => Some(d.getDropdownValues(pb))
              case _ => None
            }
          )).toList

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
              "filterName" -> JsString(t.filterName),
              "displayName" -> JsString(t.displayName),
              "filterType" -> (t.dropdownValues match {
                case Some(_) => JsString("Dropdown")
                case None => JsString(t.filterType)
              }),
              "default" -> JsString(t.defaultValue),
              "values" -> (t.dropdownValues match {
                case Some(l: List[(String, String)]) => JsArray(l.map(v => JsObject(Map(
                  "display" -> JsString(v._2),
                  "return" -> JsString(v._1)
                ))).toSeq)
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
}