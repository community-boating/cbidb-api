package com.coleji.framework.Core.Logger

class UnitTestLogger private[Core] extends Logger {
	override def trace(s: String): Unit = println(s)

	override def trace(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	override def info(s: String): Unit = println(s)

	override def info(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	override def warning(s: String): Unit = println(s)

	override def warning(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))

	override def error(s: String): Unit = println(s)

	override def error(s: String, e: Throwable): Unit = println(s + "\n" + prettyPrintException(e))
}
