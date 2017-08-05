package Services

import com.redis.RedisClient

class RedisBroker extends CacheBroker {
  val redisClient: RedisClient = new RedisClient("localhost", 6379)

  def set(key: String, value: String): Unit = redisClient.set(key, value)

  def get(key: String): Option[String] = redisClient.get(key)
}
