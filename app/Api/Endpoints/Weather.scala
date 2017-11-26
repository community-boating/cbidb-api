package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.PropertiesWrapper
import Services.ServerStateWrapper.ss
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}


class Weather @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends Controller {


  def get(): Action[AnyContent] = Action.async {request =>
    val rc: RequestCache = PermissionsAuthority.spawnRequestCache(request)
    val pb: PersistenceBroker = rc.pb
    val cb: CacheBroker = rc.cb
    val apiRequest = new WeatherRequest()
    apiRequest.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class WeatherRequest extends ApiRequest(cb) {
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
