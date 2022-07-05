package com.coleji.neptune.Core

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import com.coleji.neptune.Util.{Serde, StringUtil}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Results

import java.time.{Duration, Instant, ZoneId, ZonedDateTime}

abstract class CacheableFactory[T_KeyConfig, T_Result] {
	private val EXPIRATION_KEY_PREFIX = "$$EXP_"

	protected val lifetime: Duration

	protected def calculateKey(config: T_KeyConfig): String

	protected def generateResult(rc: RequestCache, config: T_KeyConfig): T_Result

	protected def serialize(result: T_Result): String = Serde.serializeStandard(result)

	protected def deseralize(resultString: String): T_Result = Serde.deseralizeStandard[T_Result](resultString)

	def nuke(rc: RequestCache, config: T_KeyConfig): Unit = {
		val cb = rc.cb
		val key = calculateKey(config)
		cb.delete(key)
	}

	def get(rc: RequestCache, config: T_KeyConfig): (T_Result, (ZonedDateTime, ZonedDateTime)) = {
		val cb = rc.cb
		val key = calculateKey(config)
		// TODO: some way to sync per cache key
		synchronized {
			val cacheMetadata = getMetaData(cb, key)
			if (isExpired(cacheMetadata) || !cb.peek(key)) {
				val result: T_Result = generateResult(rc, config)
				cb.set(key, serialize(result))
				val now = Instant.now
				cb.set(EXPIRATION_KEY_PREFIX + key, now.toEpochMilli.toString + ":" + now.plus(lifetime).toEpochMilli.toString)
				val newCacheMetadata = (
					ZonedDateTime.ofInstant(Instant.ofEpochMilli(now.toEpochMilli), ZoneId.systemDefault()),
					ZonedDateTime.ofInstant(Instant.ofEpochMilli(now.plus(lifetime).toEpochMilli), ZoneId.systemDefault())
				)
				(result, newCacheMetadata)
			} else {
				(deseralize(cb.get(key).get), cacheMetadata.get)
			}
		}
	}

	private def getMetaData(cb: CacheBroker, key: String): Option[(ZonedDateTime, ZonedDateTime)] = {
		cb.get(EXPIRATION_KEY_PREFIX + key).map(s => {
			StringUtil.splitAndDrop(s, ":")
		}).map(Function.tupled((created, expires) => (
			ZonedDateTime.ofInstant(Instant.ofEpochMilli(created.toLong), ZoneId.systemDefault()),
			ZonedDateTime.ofInstant(Instant.ofEpochMilli(expires.toLong), ZoneId.systemDefault())
		)))
	}

	private def isExpired(cacheMetadata: Option[(ZonedDateTime, ZonedDateTime)]): Boolean = cacheMetadata match {
		case None => true
		case Some((_, expires)) => expires.isBefore(ZonedDateTime.now)
	}
}
