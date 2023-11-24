package com.coleji.neptune.Core

import redis.clients.jedis.{Jedis, JedisPool}

import scala.util.Using

class RedisBroker private[Core](val clientPool: JedisPool)  extends CacheBroker {
	override def set(key: String, value: String): Unit = withResource(c => c.set(key, value))

	override def get(key: String): Option[String] = withResource(c => Option(c.get(key)))

	override def peek(key: String): Boolean = withResource(c => Option(c.strlen(key)).exists(_ > 0))

	override def delete(key: String): Unit = withResource(c => c.del(key))

	private def withResource[T](cmd: Jedis => T): T = {
		val t = Using (clientPool.getResource) { jedis =>
			cmd(jedis)
		}
		t.get
	}
}
