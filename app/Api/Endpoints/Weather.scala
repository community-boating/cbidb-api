package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequest
import CbiUtil.PropertiesWrapper
import Services.ServerStateWrapper.ServerState
import Services.{CacheBroker, PersistenceBroker, ServerStateWrapper}
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}


class Weather @Inject() (ssw: ServerStateWrapper, ws: WSClient) (implicit exec: ExecutionContext) extends Controller {
  implicit val ss: ServerState = ssw.get
  implicit val pb: PersistenceBroker = ss.pa.pb
  implicit val cb: CacheBroker = ss.pa.cb

  def get(): Action[AnyContent] = Action.async {
    val request = new WeatherRequest()
    request.getFuture.map(s => {
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
