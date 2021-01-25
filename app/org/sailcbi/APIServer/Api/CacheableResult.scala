package org.sailcbi.APIServer.Api

import org.sailcbi.APIServer.CbiUtil.JsonUtil
import org.sailcbi.APIServer.Services.{CacheBroker, RequestCache}
import play.api.libs.json._

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId, ZoneOffset, ZonedDateTime}
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future, Promise}


trait CacheableResult[T <: ParamsObject, U] {
	implicit val exec: ExecutionContext
	type CacheKey = String
	val cacheExpirationDatePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
	protected val cacheExpiresKeyName = "$cacheExpires"

	protected def formatTime(time: LocalDateTime): String = {
		val zonedTime: ZonedDateTime = time.atZone(ZoneId.systemDefault())
		zonedTime.withZoneSameInstant(ZoneOffset.UTC).format(cacheExpirationDatePattern)
	}

	// TODO: some way to ensure these are unique?
	def getCacheBrokerKey(params: T): CacheKey

	def getExpirationTime: LocalDateTime

	//def getJSONResultFuture(pb: PersistenceBroker, params: T): Future[JsObject]

	def getFuture(cb: CacheBroker, rc: RequestCache, params: T, calculateValue: (() => Future[JsObject])): Future[String] = {
		val finalResult: Future[String] = tryCache(cb, params) match {
			case Some(s) => {
				Future {
					s
				}
			}
			case None => {
				println("cache miss")
				tryGet(cb, rc, params, calculateValue)
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
	private def tryGet(cb: CacheBroker, rc: RequestCache, params: T, calculateValue: (() => Future[JsObject])): Future[String] = {
		val cacheKey = getCacheBrokerKey(params)
		println("here we go")
		synchronized {
			val notFirst = CacheableResult.inUse.contains(cacheKey)
			CacheableResult.inUse.add(cacheKey)
			if (notFirst) {
				println("queueing; this is queue position " + (CacheableResult.waiting.get(cacheKey) match {
					case Some(x) => x.length
					case None => 0
				}))
				// You're not the first, and whoever was the first is still computing the result.
				// They should have stashed a promise that they will complete with the result, grab it
				val p: Promise[String] = CacheableResult.resultMap(cacheKey)
				CacheableResult.waiting.get(cacheKey) match {
					case Some(x) => x += p
					case None => CacheableResult.waiting(cacheKey) = mutable.ListBuffer(p)
				}
				println("now waiting has size " + CacheableResult.waiting(cacheKey).length)
				// Nothing to do now but wait for that first person to finish getting the result.
				// Whenever that happens, return that result
				p.future.onComplete(_ => {
					CacheableResult.waiting(cacheKey) -= p
					if (CacheableResult.waiting(cacheKey).isEmpty) {
						println("$$$$$$$$  LAST ONE, removing old result")
						CacheableResult.resultMap.remove(cacheKey)
					}
				})
				p.future.onFailure({
					case _: Throwable => {
						CacheableResult.waiting(cacheKey) -= p
						if (CacheableResult.waiting(cacheKey).isEmpty) {
							CacheableResult.resultMap.remove(cacheKey)
						}
					}
				})
				p.future
			} else {
				// first person to go stores a promise that all queued's will use
				val p = Promise[String]()
				CacheableResult.resultMap.put(cacheKey, p)
				println("got the go-ahead for " + cacheKey)
				// get the result and complete the promise with it
				p.completeWith(calculateValue().map(json => {
					// Whether it crashed or not, release the hold on this cache key
					CacheableResult.inUse.remove(cacheKey)
					val newData: JsObject = json + (cacheExpiresKeyName, JsString(formatTime(getExpirationTime)))
					val result: String = new JsObject(Map("data" -> newData)).toString()
					saveToCache(cb, result, params)
					println("done son; completing all queued")
					result
				}))
				p.future.onFailure({
					case _: Throwable => {
						println("Failure detected; cleaning up")
						CacheableResult.inUse.remove(cacheKey)
					}
				})
				p.future
			}
		}
	}
}

object CacheableResult {
	var inUse: mutable.Set[String] = mutable.Set.empty
	var waiting: mutable.Map[String, mutable.ListBuffer[Promise[String]]] = mutable.Map.empty
	var resultMap: mutable.Map[String, Promise[String]] = mutable.Map.empty
}