package Api.Endpoints

import java.time.LocalDateTime
import javax.inject.Inject

import Api.ApiRequestAsync
import CbiUtil.PropertiesWrapper
import Services.CacheBroker
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}


class Weather @Inject() (cb: CacheBroker, ws: WSClient)(implicit exec: ExecutionContext) extends Controller {
  def get() = Action.async {
    val request = new WeatherRequest()
    request.getFuture.map(s => {
      Ok(s).as("application/json")
    })
  }

  class WeatherRequest extends ApiRequestAsync(cb) {
    def getCacheBrokerKey: String = "weather"

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
