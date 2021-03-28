package com.coleji.framework.Core.Logger

class UnitTestLogger private[Core] extends Logger {
	def trace(s: String): Unit = println(s)

	def trace(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	def info(s: String): Unit = println(s)

	def info(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	def warning(s: String): Unit = println(s)

	def warning(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	def error(s: String): Unit = println(s)

	def error(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))
}
