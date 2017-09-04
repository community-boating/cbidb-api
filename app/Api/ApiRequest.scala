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

  // TODO: on cache miss, first request get the right to talk to the database
  // all other requests wait for it to finish, semaphore stores them in a queue
  // On complete, write to redis, then give result to all waiting threads
  // Once written to redis, cache will hit and no further threads will enter the queue (until next stale event)
  var inUse: mutable.Set[CacheKey] = mutable.Set.empty
  var waiting: mutable.Map[CacheKey, List[Promise[String]]] = mutable.Map.empty
  var resultMap: mutable.Map[CacheKey, Future[String]] = _

  def tryGet: Future[String] = {
    println("here we go")
    if (inUse.contains(getCacheBrokerKey)) {
      println("queueing; this is queue position " + waiting(getCacheBrokerKey).length)
      val p: Promise[String] = Promise[String]()
      waiting ++ (getCacheBrokerKey -> p :: waiting(getCacheBrokerKey))
      p.completeWith(resultMap(getCacheBrokerKey))
      resultMap(getCacheBrokerKey)
    } else {
      inUse.add(getCacheBrokerKey)
      println("got the go-ahead")
      val jsonFuture: Future[JsObject] = getJSONResultFuture
      val stringFuture: Future[String] = jsonFuture.map(json => {
        val newData: JsObject = json + (cacheExpiresKeyName, JsString(formatTime(getExpirationTime)))
        val result: String = new JsObject(Map("data" -> newData)).toString()
        saveToCache(result)
        inUse.remove(getCacheBrokerKey)
        println("done son; completing all queued")

        result
      })
      resultMap(getCacheBrokerKey) = stringFuture
      stringFuture
    }
  }

}
