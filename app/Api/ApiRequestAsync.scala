package Api

import Services.CacheBroker
import play.api.libs.json.{JsObject, JsString}

import scala.concurrent.{ExecutionContext, Future, Promise}

abstract class ApiRequestAsync (cb: CacheBroker)(implicit exec: ExecutionContext) extends ApiRequest(cb) {
  def getJSONResultFuture: Future[JsObject]

  def getFuture: Future[String] = {
    val finalResult: Future[String] = tryCache match {
      case Some(s) => {
        Future{s}
      }
      case None => {
        println("cache miss")
        persistenceSemaphore.tryGet
      }
    }
    finalResult
  }

  // TODO: on cache miss, first request get the right to talk to the database
  // all other requests wait for it to finish, semaphore stores them in a queue
  // On complete, write to redis, then give result to all waiting threads
  // Once written to redis, cache will hit and no further threads will enter the queue (until next stale event)
  object persistenceSemaphore {
    var inUse = false
    var waiting: List[Promise[String]] = List.empty
    var result: Future[String] = _

    def tryGet: Future[String] = {
      println("here we go")
      if (inUse) {
        println("queueing; this is queue position " + waiting.length)
        val p: Promise[String] = Promise[String]()
        waiting = p :: waiting
        p.completeWith(result)
        result
      } else {
        inUse = true
        println("got the go-ahead")
        val jsonFuture: Future[JsObject] = getJSONResultFuture
        result = jsonFuture.map(json => {
          val newData: JsObject = json + (cacheExpiresKeyName, JsString(formatTime(getExpirationTime)))
          val result: String = new JsObject(Map("data" -> newData)).toString()
          saveToCache(result)
          inUse = false
          println("done son; completing all queued")
          result
        })
        result
      }
    }
  }
}
