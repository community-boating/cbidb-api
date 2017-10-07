package Services

import com.redis.RedisClientPool

class RedisBroker extends CacheBroker {
  val clientPool = new RedisClientPool("localhost", 6379)

  def set(key: String, value: String): Unit = clientPool.withClient(c => c.set(key, value))

  def get(key: String): Option[String] = clientPool.withClient(c => c.get(key))
}
