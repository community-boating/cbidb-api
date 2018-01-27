package Api.Endpoints.ReportingAPI

import java.time.LocalDateTime
import javax.inject.Inject

import Api.CacheableRequest
import CbiUtil.ParsedRequest
import Reporting.ReportingFilters._
import Reporting.{Report, ReportFactory}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services._
import Storable.StorableClass
import play.api.libs.json.{JsArray, JsBoolean, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller, Result}

import scala.concurrent.{ExecutionContext, Future}

class GetReportRunOptions @Inject() (implicit exec: ExecutionContext) extends Controller {
  /*
    def get(): Action[AnyContent] = Action.async {r => doGet(ParsedRequest(r))}

    def doGet(req: ParsedRequest): Future[Result] = {
      try {
        val rc: RequestCache = PermissionsAuthority.getRequestCache(req.headers, req.cookies)
        val pb: PersistenceBroker = rc.pb
        val cb: CacheBroker = rc.cb
        val apiRequest = new ReportRunOptionsRequest(pb, cb)
        apiRequest.getFuture.map(s => {
          Ok(s).as("application/json")
        })
      } catch {
        case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
        case _: Throwable => Future{ Ok("Internal Error") }
      }
    }

  class ReportRunOptionsRequest(pb: PersistenceBroker, cb: CacheBroker) extends CacheableRequest(cb) {
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
            f._2.argDefinitions.map(_._1).map({
              case ARG_INT => "Int"
              case ARG_DOUBLE => "Double"
              case ARG_DATE => "Date"
              case ARG_DROPDOWN => "Dropdown"
              case t: Any => throw new Exception("Unconfigured arg type " + t)
            }).mkString(","),
            f._2.argDefinitions.map(_._2).mkString(","),
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
              "filterType" -> JsString(t.filterType),
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
  */
}