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

  // TODO: on cache miss, first request get the right to talk to the database
  // all other requests wait for it to finish, semaphore stores them in a queue
  // On complete, write to redis, then give result to all waiting threads
  // Once written to redis, cache will hit and no further threads will enter the queue (until next stale event)
  object persistenceSemaphore {

  }
}
