package com.coleji.neptune.Core

abstract class CacheBroker private[Core] {
	def set(key: String, value: String): Unit

	def get(key: String): Option[String]

	def peek(key: String): Boolean
}
