package Services

abstract class CacheBroker private[Services] {
	def set(key: String, value: String): Unit

	def get(key: String): Option[String]
}
