package Api

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId, ZoneOffset, ZonedDateTime}

import CbiUtil.JsonUtil
import Services.CacheBroker
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}


abstract class ApiRequest(cb: CacheBroker)(implicit exec: ExecutionContext) {
  type CacheKey = String
  val cacheExpirationDatePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
  protected val cacheExpiresKeyName = "$cacheExpires"
  protected def formatTime(time: LocalDateTime): String = {
    val zonedTime: ZonedDateTime = time.atZone(ZoneId.systemDefault())
    zonedTime.withZoneSameInstant(ZoneOffset.UTC).format(cacheExpirationDatePattern)
  }

  def getCacheBrokerKey: CacheKey
  def getExpirationTime: LocalDateTime

  def tryCache: Option[String] = {
    cb.get(getCacheBrokerKey) match {
      case Some(s) => {
        println("cache hit")
        val value: JsValue = Json.parse(s)
        val data: JsObject = JsonUtil.getProperty[JsObject](value, "data")
        val cacheExpiration: String = JsonUtil.getProperty[JsString](data, cacheExpiresKeyName).value
        val expires: ZonedDateTime =
          ZonedDateTime
          .parse(cacheExpiration, cacheExpirationDatePattern)
          .withZoneSameInstant(ZoneId.systemDefault)
        if (expires.isBefore(ZonedDateTime.now)) None
        else Some(s)
      }
      case None => None
    }
  }

  def saveToCache(result: String): Unit = cb.set(getCacheBrokerKey, result)

  def getJSONResultFuture: Future[JsObject]

  def getFuture: Future[String] = {
    val finalResult: Future[String] = tryCache match {
      case Some(s) => {
        Future{s}
      }
      case None => {
        println("cache miss")
        tryGet
      }
    }
    finalResult
  }

  // basically works.  Not convinced its 100% threadsafe under heavy parallel load
  // TODO: confirm crash recovery works, especially when there are queued waiters
  // TODO: if waiters are waiting and a crash happens, they should all try themselves?
  def tryGet: Future[String] = {
    println("here we go")
    synchronized {
      val notFirst = ApiRequest.inUse.contains(getCacheBrokerKey)
      ApiRequest.inUse.add(getCacheBrokerKey)
      if (notFirst) {
        println("queueing; this is queue position " + (ApiRequest.waiting.get(getCacheBrokerKey) match {
          case Some(x) => x.length
          case None => 0
        }))
        // You're not the first, and whoever was the first is still computing the result.
        // They should have stashed a promise that they will complete with the result, grab it
        val p: Promise[String] = ApiRequest.resultMap(getCacheBrokerKey)
        ApiRequest.waiting.get(getCacheBrokerKey) match {
          case Some(x) => x += p
          case None => ApiRequest.waiting(getCacheBrokerKey) = mutable.ListBuffer(p)
        }
        println("now waiting has size " + ApiRequest.waiting(getCacheBrokerKey).length)
        // Nothing to do now but wait for that first person to finish getting the result.
        // Whenever that happens, return that result
        p.future.onComplete(_ => {
          ApiRequest.waiting(getCacheBrokerKey) -= p
          if (ApiRequest.waiting(getCacheBrokerKey).isEmpty) {
            println("$$$$$$$$  LAST ONE, removing old result")
            ApiRequest.resultMap.remove(getCacheBrokerKey)
          }
        })
        p.future.onFailure({
          case _: Throwable => {
            ApiRequest.waiting(getCacheBrokerKey) -= p
            if (ApiRequest.waiting(getCacheBrokerKey).isEmpty) {
              ApiRequest.resultMap.remove(getCacheBrokerKey)
            }
          }
        })
        p.future
      } else {
        // first person to go stores a promise that all queued's will use
        val p = Promise[String]()
        ApiRequest.resultMap.put(getCacheBrokerKey, p)
        println("got the go-ahead for " + getCacheBrokerKey)
        // get the result and complete the promise with it
        p.completeWith(getJSONResultFuture.map(json => {
          // Whether it crashed or not, release the hold on this cache key
          ApiRequest.inUse.remove(getCacheBrokerKey)
          val newData: JsObject = json + (cacheExpiresKeyName, JsString(formatTime(getExpirationTime)))
          val result: String = new JsObject(Map("data" -> newData)).toString()
          saveToCache(result)
          println("done son; completing all queued")
          result
        }))
        p.future.onFailure({
          case _: Throwable => {
            println("Failure detected; cleaning up")
            ApiRequest.inUse.remove(getCacheBrokerKey)
          }
        })
        p.future
      }
    }
  }

}

object ApiRequest {
  var inUse: mutable.Set[String] = mutable.Set.empty
  var waiting: mutable.Map[String, mutable.ListBuffer[Promise[String]]] = mutable.Map.empty
  var resultMap: mutable.Map[String, Promise[String]] = mutable.Map.empty
}