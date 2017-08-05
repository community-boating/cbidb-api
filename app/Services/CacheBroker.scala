package Services

abstract class CacheBroker {
  def set(key: String, value: String): Unit

  def get(key: String): Option[String]
}
