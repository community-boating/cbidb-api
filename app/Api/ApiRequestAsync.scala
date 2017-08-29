package Api

import Services.CacheBroker
import play.api.libs.json.{JsObject, JsString}

import scala.concurrent.{ExecutionContext, Future}

abstract class ApiRequestAsync (cb: CacheBroker)(implicit exec: ExecutionContext) extends ApiRequest(cb) {
  def getJSONResultFuture: Future[JsObject]

  def getFuture: Future[String] = {
    val finalResult: Future[String] = tryCache match {
      case Some(s) => {
        Future{s}
      }
      case None => {
        println("cache miss")
        val jsonFuture: Future[JsObject] = getJSONResultFuture
        val next: Future[String] = jsonFuture.map(json => {
          val newData: JsObject = json + (cacheExpiresKeyName, JsString(formatTime(getExpirationTime)))
          val result: String = new JsObject(Map("data" -> newData)).toString()
          saveToCache(result)
          result
        })
        next
      }
    }
    finalResult

  }
}
