package Api.Endpoints.ReportingAPI

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import Reporting.ReportingFilters.{ARG_DROPDOWN, ARG_INT, ReportingFilterFactoryDropdown}
import Reporting.{Report, ReportFactory}
import Services.ServerStateWrapper.ServerState
import Services.{CacheBroker, PersistenceBroker, ServerStateWrapper}
import Storable.StorableClass
import play.api.libs.json.{JsArray, JsBoolean, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class GetReportRunOptions @Inject() (ssw: ServerStateWrapper) (implicit exec: ExecutionContext) extends Controller {
  implicit val ss: ServerState = ssw.get
  implicit val pb: PersistenceBroker = ss.pa.pb
  implicit val cb: CacheBroker = ss.pa.cb

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
        dropdownValues: Option[List[List[(String, String)]]]
      )
      val resultData: JsArray = Report.reportFactoryMap.foldLeft(new JsArray)((arr, e) => {
        val entityName: String = e._1
        val entityDisplayName: String = e._2._1

        val factoryInstance: ReportFactory[_ <: StorableClass] =
          Class.forName(e._2._2.getCanonicalName).newInstance.asInstanceOf[ReportFactory[_ <: StorableClass]]

        // Field name and display name
        val fieldData: List[(String, String, Boolean)] = factoryInstance.fieldList.map(f => (f._1, f._2.fieldDisplayName, f._2.isDefault))
        val filterData: List[FilterDataForJSON] =
          factoryInstance.filterList.map(f => FilterDataForJSON(
            f._1,
            f._2.displayName,
            f._2.argTypes.map({
              case ARG_INT => "Int"
              case ARG_DROPDOWN => "Dropdown"
              case t: Any => throw new Exception("Unconfigured arg type " + t)
            }).mkString(","),
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
              "fieldDisplayName" -> JsString(t._2),
              "isDefault" -> JsBoolean(t._3)
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
                case Some(ll: List[List[(String, String)]]) => JsArray(ll.map(l => JsArray(l.map(v => JsObject(Map(
                  "display" -> JsString(v._2),
                  "return" -> JsString(v._1)
                ))))))
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