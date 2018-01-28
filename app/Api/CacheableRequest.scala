package Api

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId, ZoneOffset, ZonedDateTime}

import CbiUtil.JsonUtil
import Logic.PreparedQueries.PreparedQueryCastableToJSObject
import Services.{CacheBroker, PersistenceBroker}
import play.api.libs.json._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}


sealed abstract class CacheableRequest[T <: ParamsObject, U <: ApiDataObject](implicit exec: ExecutionContext) {
  type CacheKey = String
  type PQ = PreparedQueryCastableToJSObject[U]
  val cacheExpirationDatePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
  protected val cacheExpiresKeyName = "$cacheExpires"
  protected def formatTime(time: LocalDateTime): String = {
    val zonedTime: ZonedDateTime = time.atZone(ZoneId.systemDefault())
    zonedTime.withZoneSameInstant(ZoneOffset.UTC).format(cacheExpirationDatePattern)
  }

  def getCacheBrokerKey(params: T): CacheKey
  def getExpirationTime: LocalDateTime

  //def getJSONResultFuture(pb: PersistenceBroker, params: T): Future[JsObject]

  def getFuture(cb: CacheBroker, pb: PersistenceBroker, params: T, calculateValue: (() => Future[JsObject])): Future[String] = {
    val finalResult: Future[String] = tryCache(cb, params) match {
      case Some(s) => {
        Future{s}
      }
      case None => {
        println("cache miss")
        tryGet(cb, pb, params, calculateValue)
      }
    }
    finalResult
  }

  private def tryCache(cb: CacheBroker, params: T): Option[String] = {
    val cacheKey = getCacheBrokerKey(params)
    cb.get(cacheKey) match {
      case Some(s) => {
        println("cache hit: " + cacheKey)
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

  private def saveToCache(cb: CacheBroker, result: String, params: T): Unit = cb.set(getCacheBrokerKey(params), result)

  // basically works.  Not convinced its 100% threadsafe under heavy parallel load
  // TODO: confirm crash recovery works, especially when there are queued waiters
  // TODO: if waiters are waiting and a crash happens, they should all try themselves?
  private def tryGet(cb: CacheBroker, pb: PersistenceBroker, params: T, calculateValue: (() => Future[JsObject])): Future[String] = {
    val cacheKey = getCacheBrokerKey(params)
    println("here we go")
    synchronized {
      val notFirst = CacheableRequest.inUse.contains(cacheKey)
      CacheableRequest.inUse.add(cacheKey)
      if (notFirst) {
        println("queueing; this is queue position " + (CacheableRequest.waiting.get(cacheKey) match {
          case Some(x) => x.length
          case None => 0
        }))
        // You're not the first, and whoever was the first is still computing the result.
        // They should have stashed a promise that they will complete with the result, grab it
        val p: Promise[String] = CacheableRequest.resultMap(cacheKey)
        CacheableRequest.waiting.get(cacheKey) match {
          case Some(x) => x += p
          case None => CacheableRequest.waiting(cacheKey) = mutable.ListBuffer(p)
        }
        println("now waiting has size " + CacheableRequest.waiting(cacheKey).length)
        // Nothing to do now but wait for that first person to finish getting the result.
        // Whenever that happens, return that result
        p.future.onComplete(_ => {
          CacheableRequest.waiting(cacheKey) -= p
          if (CacheableRequest.waiting(cacheKey).isEmpty) {
            println("$$$$$$$$  LAST ONE, removing old result")
            CacheableRequest.resultMap.remove(cacheKey)
          }
        })
        p.future.onFailure({
          case _: Throwable => {
            CacheableRequest.waiting(cacheKey) -= p
            if (CacheableRequest.waiting(cacheKey).isEmpty) {
              CacheableRequest.resultMap.remove(cacheKey)
            }
          }
        })
        p.future
      } else {
        // first person to go stores a promise that all queued's will use
        val p = Promise[String]()
        CacheableRequest.resultMap.put(cacheKey, p)
        println("got the go-ahead for " + cacheKey)
        // get the result and complete the promise with it
        p.completeWith(calculateValue().map(json => {
          // Whether it crashed or not, release the hold on this cache key
          CacheableRequest.inUse.remove(cacheKey)
          val newData: JsObject = json + (cacheExpiresKeyName, JsString(formatTime(getExpirationTime)))
          val result: String = new JsObject(Map("data" -> newData)).toString()
          saveToCache(cb, result, params)
          println("done son; completing all queued")
          result
        }))
        p.future.onFailure({
          case _: Throwable => {
            println("Failure detected; cleaning up")
            CacheableRequest.inUse.remove(cacheKey)
          }
        })
        p.future
      }
    }
  }
}

abstract class CacheableRequestFromPreparedQuery[T <: ParamsObject, U <: ApiDataObject](implicit exec: ExecutionContext) extends CacheableRequest[T, U] {
  def getFuture(cb: CacheBroker, pb: PersistenceBroker, params: T, pq: PQ): Future[String] = {
    val calculateValue: (() => Future[JsObject]) = () => Future {
      val queryResults = pb.executePreparedQuery(pq)

      JsObject(Map(
        "rows" -> JsArray(queryResults.map(r => pq.mapCaseObjectToJsArray(r))),
        "metaData" -> pq.getColumnsNamesAsJSObject
      ))
    }
    getFuture(cb, pb, params, calculateValue)
  }
}


object CacheableRequest {
  var inUse: mutable.Set[String] = mutable.Set.empty
  var waiting: mutable.Map[String, mutable.ListBuffer[Promise[String]]] = mutable.Map.empty
  var resultMap: mutable.Map[String, Promise[String]] = mutable.Map.empty
}