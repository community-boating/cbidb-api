package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.PropertiesWrapper
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}


class Weather @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends Controller {


  def get(): Action[AnyContent] = Action.async {request =>
    try {
      val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
      val pb: PersistenceBroker = rc.pb
      val cb: CacheBroker = rc.cb
      val apiRequest = new WeatherRequest(pb, cb)
      apiRequest.getFuture.map(s => {
        Ok(s).as("application/json")
      })
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("Access Denied") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

  class WeatherRequest(pb: PersistenceBroker, cb: CacheBroker) extends ApiRequest(cb) {
    def getCacheBrokerKey: CacheKey = "weather"

    def getExpirationTime: LocalDateTime = {
      LocalDateTime.now.plusMinutes(10)
    }

    object params {}

    def getJSONResultFuture: Future[JsObject] = {
      val props = new PropertiesWrapper("conf/private/weather-credentials", Array[String]("host", "path"))
      println("Making request to remote weather service")
      val request: WSRequest = ws.url("https://" + props.getProperty("host") + props.getProperty("path"))
      val futureResponse: Future[WSResponse] = request.get()
      val jsonFuture: Future[JsObject] = futureResponse.map(_.json match {
        case json: JsObject => json
        case v: JsValue => new JsObject(Map("data" -> v))
        case _ =>  throw new Exception("Received unparsable json response")
      })
      jsonFuture
    }
  }
}
