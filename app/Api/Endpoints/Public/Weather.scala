package Api.Endpoints.Public

import java.time.LocalDateTime
import javax.inject.Inject

import Api.{AuthenticatedRequest, CacheableResultFromPreparedQuery, CacheableResultFromRemoteRequest}
import Api.Endpoints.Public.JpTeams.JpTeamsParamsObject
import CbiUtil.PropertiesWrapper
import Logic.PreparedQueries.Public.{GetJpTeams, GetJpTeamsResult}
import Services.Authentication.PublicUserType
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext


class Weather @Inject() (ws: WSClient) (implicit val exec: ExecutionContext)
extends AuthenticatedRequest with CacheableResultFromRemoteRequest[JpTeamsParamsObject, GetJpTeamsResult] {
  def get: Action[AnyContent] = {
    evaluate(PublicUserType, new JpTeamsParamsObject, ws, Weather.url)
  }

  def getCacheBrokerKey(params: JpTeamsParamsObject): CacheKey = "weather"

  def getExpirationTime: LocalDateTime = {
    LocalDateTime.now.plusMinutes(10)
  }
/*

  def get(): Action[AnyContent] = Action.async {request =>
    try {
      val rc: RequestCache = PermissionsAuthority.getRequestCache(request.headers, request.cookies)
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

  class WeatherRequest(pb: PersistenceBroker, cb: CacheBroker) extends CacheableRequest(cb) {
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
  */
}

object Weather {
  val props = new PropertiesWrapper("conf/private/weather-credentials", Array[String]("host", "path"))
  println("Making request to remote weather service")
  val url = "https://" + props.getProperty("host") + props.getProperty("path")
}
