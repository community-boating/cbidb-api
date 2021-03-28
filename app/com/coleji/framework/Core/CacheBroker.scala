package com.coleji.framework.Core

abstract class CacheBroker private[Core] {
	def set(key: String, value: String): Unit

	def get(key: String): Option[String]
}
