package com.coleji.framework.Core

import com.redis.RedisClientPool

class RedisBroker private[Core] extends CacheBroker {
	def set(key: String, value: String): Unit = RedisBroker.clientPool.withClient(c => c.set(key, value))

	def get(key: String): Option[String] = RedisBroker.clientPool.withClient(c => c.get(key))
}

object RedisBroker {
	val clientPool = new RedisClientPool("localhost", 6379)
}