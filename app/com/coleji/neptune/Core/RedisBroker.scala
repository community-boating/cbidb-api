package com.coleji.neptune.Core

import com.redis.RedisClientPool

class RedisBroker private[Core](val clientPool: RedisClientPool)  extends CacheBroker {
	def set(key: String, value: String): Unit = clientPool.withClient(c => c.set(key, value))

	def get(key: String): Option[String] = clientPool.withClient(c => c.get(key))
}
