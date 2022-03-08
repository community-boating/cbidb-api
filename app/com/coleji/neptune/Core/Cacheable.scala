package com.coleji.neptune.Core

import com.coleji.neptune.Util.Serde

import java.time.{Duration, Instant}

abstract class Cacheable[T_KeyConfig, T_Result] {
	private val EXPIRATION_KEY_PREFIX = "$$EXP_"

	protected val lifetime: Duration

	protected def calculateKey(config: T_KeyConfig): String

	protected def generateResult(rc: RequestCache, config: T_KeyConfig): T_Result

	protected def serialize(result: T_Result): String = Serde.serializeStandard(result)

	protected def deseralize(resultString: String): T_Result = Serde.deseralizeStandard[T_Result](resultString)

	def get(rc: RequestCache, config: T_KeyConfig): T_Result = {
		val cb = rc.cb
		val key = calculateKey(config)
		// TODO: some way to sync per cache key
		synchronized {
			if (isExpired(cb, key)) {
				val result: T_Result = generateResult(rc, config)
				cb.set(key, serialize(result))
				cb.set(EXPIRATION_KEY_PREFIX + key, Instant.now.plus(lifetime).toEpochMilli.toString)
				result
			} else {
				deseralize(cb.get(key).get)
			}
		}
	}

	private def isExpired(cb: CacheBroker, key: String): Boolean = {
		cb.get(EXPIRATION_KEY_PREFIX + key) match {
			case None => true
			case Some(time: String) => Instant.ofEpochMilli(time.toLong).isBefore(Instant.now) && cb.peek(key)
		}
	}
}
