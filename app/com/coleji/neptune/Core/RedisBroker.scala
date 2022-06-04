package com.coleji.neptune.Core

import com.redis.RedisClientPool

class RedisBroker private[Core](val clientPool: RedisClientPool)  extends CacheBroker {
	override def set(key: String, value: String): Unit = clientPool.withClient(c => c.set(key, value))

	override def get(key: String): Option[String] = clientPool.withClient(c => c.get(key))

	override def peek(key: String): Boolean = clientPool.withClient(c => c.strlen(key).exists(_ > 0))
}
